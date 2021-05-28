package pers.acp.admin.deploy.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.constant.TokenConstant
import pers.acp.admin.deploy.conf.DeployServerCustomerConfiguration
import pers.acp.admin.deploy.entity.DeployTask
import pers.acp.admin.deploy.po.DeployTaskPo
import pers.acp.admin.deploy.po.DeployTaskQueryPo
import pers.acp.admin.deploy.repo.DeployTaskRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.cloud.component.CloudTools
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import java.io.File
import java.io.InputStreamReader
import java.io.LineNumberReader
import javax.persistence.criteria.Predicate
import javax.sql.DataSource

@Service
@Transactional(readOnly = true)
class DeployTaskDomain @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val cloudTools: CloudTools,
    private val dataSource: DataSource,
    private val commonOauthServer: CommonOauthServer,
    private val deployTaskRepository: DeployTaskRepository,
    private val deployServerCustomerConfiguration: DeployServerCustomerConfiguration
) : BaseDomain() {
    @Throws(ServerException::class)
    private fun makeFold(): File = File(deployServerCustomerConfiguration.scriptPath).apply {
        if (!this.exists()) {
            if (!this.mkdirs()) {
                logAdapter.error("创建目录失败: " + this.canonicalPath)
                throw ServerException("创建目录失败！")
            }
        }
    }

    private fun copyEntity(deployTask: DeployTask, deployTaskPo: DeployTaskPo): DeployTask =
        deployTask.copy(
            name = deployTaskPo.name!!,
            serverIpRegex = deployTaskPo.serverIpRegex,
            remarks = deployTaskPo.remarks
        ).apply {
            var targetFileName = deployTaskPo.scriptFile!!.replace("/", File.separator).replace("\\", File.separator)
            val index = targetFileName.lastIndexOf(File.separator)
            if (index > -1) {
                targetFileName = targetFileName.substring(index + 1)
            }
            this.scriptFile = targetFileName.replace(Regex("[\\\\<>/|:\"*?]"), "")
            commonOauthServer.tokenInfo()?.also { oAuth2AccessToken ->
                val nowTime = System.currentTimeMillis()
                if (this.createTime == 0L) {
                    this.createLoginNo =
                        oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_LOGIN_NO]!!.toString()
                    this.createUserName =
                        oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_NAME]!!.toString()
                    this.createTime = nowTime
                }
            } ?: throw ServerException("获取当前登录用户信息失败")
        }

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(deployTaskPo: DeployTaskPo): DeployTask =
        deployTaskRepository.save(copyEntity(DeployTask(), deployTaskPo))

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(deployTaskPo: DeployTaskPo): DeployTask =
        deployTaskRepository.save(copyEntity(deployTaskRepository.getOne(deployTaskPo.id!!), deployTaskPo))

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(idList: MutableList<String>) =
        deployTaskRepository.deleteByIdIn(idList)

    @Transactional
    @Throws(ServerException::class)
    fun executeTask(id: String) = deployTaskRepository.findByIdOrNull(id)?.let { deployTask ->
        commonOauthServer.tokenInfo()?.let { oAuth2AccessToken ->
            val nowTime = System.currentTimeMillis()
            if (deployTask.execTime == null) {
                deployTask.execLoginNo =
                    oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_LOGIN_NO]!!.toString()
                deployTask.execUserName =
                    oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_NAME]!!.toString()
                deployTask.execTime = nowTime
                deployTaskRepository.save(deployTask)
            } else {
                throw ServerException("任务${deployTask.name}已被执行过！")
            }
        } ?: throw ServerException("获取当前登录用户信息失败")
    } ?: throw ServerException("找不到对应的部署任务【$id】")

    @Transactional
    @Throws(ServerException::class)
    fun doExecuteTask(id: String) = deployTaskRepository.findByIdOrNull(id)?.let { deployTask ->
        if (!CommonTools.isNullStr(deployTask.serverIpRegex) && !CommonTools.regexPattern(
                deployTask.serverIpRegex!!,
                cloudTools.getServerIp()
            )
        ) {
            logAdapter.info("当前实例服务器IP【${cloudTools.getServerIp()}】不匹配【${deployTask.serverIpRegex}】，不执行部署任务！")
        } else {
            val fold = makeFold()
            val scriptFile = File("${fold.canonicalPath}${File.separator}${deployTask.scriptFile}")
            try {
                if (!scriptFile.exists()) {
                    throw ServerException("文件【${scriptFile.canonicalPath}】不存在！")
                }
                logAdapter.info("开始执行脚本【${scriptFile.canonicalPath}】")
                when (CommonTools.getFileExt(scriptFile.name).toLowerCase()) {
                    "sh" -> {
                        Runtime.getRuntime().exec("chmod +x ${scriptFile.canonicalPath}").waitFor()
                        Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", scriptFile.canonicalPath), null, null)
                            .apply {
                                executeScriptFile(this, scriptFile)
                            }
                    }
                    "bat" -> {
                        Runtime.getRuntime().exec(scriptFile.canonicalPath)
                    }
                    "sql" -> {
                        ResourceDatabasePopulator().apply {
                            this.addScript(FileSystemResource(scriptFile))
                            this.execute(dataSource)
                        }
                    }
                    else -> {
                        throw ServerException("脚本【${scriptFile.canonicalPath}】不能执行，仅支持bat/sh/sql文件")
                    }
                }
            } catch (e: Exception) {
                throw ServerException("脚本【${scriptFile.canonicalPath}】${e.message}")
            }
        }
    } ?: throw ServerException("找不到对应的部署任务【$id】")

    /**
     * 执行脚本文件
     */
    private fun executeScriptFile(process: Process, scriptFile: File) {
        val reader = InputStreamReader(process.inputStream)
        val input = LineNumberReader(reader)
        var line: String?
        while (input.readLine().also { line = it } != null) {
            logAdapter.info("${scriptFile.name} >>>> $line")
        }
        process.waitFor()
        input.close()
    }

    fun doQuery(deployTaskQueryPo: DeployTaskQueryPo): Page<DeployTask> =
        deployTaskRepository.findAll({ root, _, criteriaBuilder ->
            val predicateList: MutableList<Predicate> = mutableListOf()
            if (!CommonTools.isNullStr(deployTaskQueryPo.name)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("name").`as`(String::class.java),
                        "%" + deployTaskQueryPo.name + "%"
                    )
                )
            }
            if (deployTaskQueryPo.startTime != null) {
                predicateList.add(
                    criteriaBuilder.ge(
                        root.get<Any>("execTime").`as`(Long::class.java),
                        deployTaskQueryPo.startTime
                    )
                )
            }
            if (deployTaskQueryPo.endTime != null) {
                predicateList.add(
                    criteriaBuilder.le(
                        root.get<Any>("execTime").`as`(Long::class.java),
                        deployTaskQueryPo.endTime
                    )
                )
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }, buildPageRequest(deployTaskQueryPo.queryParam!!))
}