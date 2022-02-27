package pers.acp.admin.deploy.domain

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.cloud.component.CloudTools
import io.github.zhangbinhub.acp.core.CommonTools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
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
import java.io.*
import java.nio.charset.Charset
import java.util.*
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

    private fun formatFileName(srcFileName: String): String =
        srcFileName.replace("/", File.separator).replace("\\", File.separator).let { targetFileName ->
            val index = targetFileName.lastIndexOf(File.separator)
            if (index > -1) {
                targetFileName.substring(index + 1)
            } else {
                targetFileName
            }
        }.replace(Regex("[\\\\<>/|:\"*?]"), "")

    @Throws(ServerException::class)
    private fun getParamList(fold: File, paramFileName: String?): List<List<String>> =
        if (!CommonTools.isNullStr(paramFileName)) {
            val paramFile = File("${fold.canonicalPath}${File.separator}${paramFileName}")
            if (!paramFile.exists()) {
                throw ServerException("文件【${paramFile.canonicalPath}】不存在！")
            }
            var sc: Scanner? = null
            try {
                sc = Scanner(FileInputStream(paramFile), Charset.forName(CommonTools.getDefaultCharset()))
                mutableListOf<List<String>>().apply {
                    while (sc.hasNextLine()) {
                        this.add(sc.nextLine().split(deployServerCustomerConfiguration.paramSeparator))
                    }
                }
            } catch (e: Exception) {
                throw ServerException("参数文件【${paramFile.canonicalPath}】解析失败:${e.message}")
            } finally {
                try {
                    sc?.close()
                } catch (e: IOException) {
                    throw ServerException("参数文件【${paramFile.canonicalPath}】解析失败:${e.message}")
                }
            }
        } else {
            listOf()
        }

    private fun copyEntity(deployTask: DeployTask, deployTaskPo: DeployTaskPo): DeployTask =
        deployTask.copy(
            name = deployTaskPo.name!!,
            serverIpRegex = deployTaskPo.serverIpRegex,
            remarks = deployTaskPo.remarks
        ).apply {
            this.scriptFile = formatFileName(deployTaskPo.scriptFile!!)
            if (!CommonTools.isNullStr(deployTaskPo.paramFile)) {
                this.paramFile = formatFileName(deployTaskPo.paramFile!!)
            } else {
                this.paramFile = deployTaskPo.paramFile
            }
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
        deployTaskRepository.save(copyEntity(deployTaskRepository.getById(deployTaskPo.id!!), deployTaskPo))

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
                val paramList = getParamList(fold, deployTask.paramFile)
                if (paramList.isNotEmpty()) {
                    paramList.forEachIndexed { index, list ->
                        val targetScriptFile = buildTargetScriptFile(index, list, scriptFile)
                        logAdapter.info("开始第${index + 1}/${paramList.size}次执行脚本【${scriptFile.canonicalPath}】")
                        logAdapter.info("参数：${list.joinToString(separator = ",")}")
                        logAdapter.info("目标脚本：${targetScriptFile.canonicalPath}")
                        executeScriptFile(targetScriptFile)
                    }
                } else {
                    executeScriptFile(scriptFile)
                }
            } catch (e: Exception) {
                throw ServerException("脚本【${scriptFile.canonicalPath}】${e.message}")
            }
        }
    } ?: throw ServerException("找不到对应的部署任务【$id】")

    /**
     * 根据参数创建新的脚本文件
     * @param paramIndex 参数索引
     * @param paramList 参数值列表
     * @param scriptFile 原脚本文件
     * @return 可执行的目标脚本文件
     */
    @Throws(ServerException::class)
    private fun buildTargetScriptFile(paramIndex: Int, paramList: List<String>, scriptFile: File): File =
        scriptFile.name.let { scriptFileName ->
            val fileName = scriptFileName.substring(0, scriptFileName.lastIndexOf("."))
            val extName = scriptFileName.substring(scriptFileName.lastIndexOf(".") + 1)
            "$fileName$paramIndex.$extName"
        }.let { fileName ->
            File("${scriptFile.parentFile.canonicalPath}${File.separator}run${File.separator}$fileName").apply {
                if (!this.parentFile.exists()) {
                    if (!this.parentFile.mkdirs()) {
                        logAdapter.error("创建目录失败: " + this.canonicalPath)
                        throw ServerException("创建目录失败！")
                    }
                }
            }
        }.apply {
            CommonTools.getFileContentForText(scriptFile.canonicalPath)?.also { scriptContent ->
                var targetScriptContent = scriptContent
                paramList.forEachIndexed { index, param ->
                    targetScriptContent = targetScriptContent.replace(
                        "${deployServerCustomerConfiguration.paramCharacterPrefix}$index",
                        param
                    )
                }
                CommonTools.contentWriteToFile(this, targetScriptContent)
            } ?: throw ServerException("脚本文件【${scriptFile.canonicalPath}】内容为空！")
        }

    /**
     * 执行脚本文件
     */
    @Throws(ServerException::class)
    private fun executeScriptFile(scriptFile: File) {
        when (CommonTools.getFileExt(scriptFile.name).lowercase()) {
            "sh" -> {
                Runtime.getRuntime().exec("chmod +x ${scriptFile.canonicalPath}").waitFor()
                Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", scriptFile.canonicalPath), null, null)
                    .apply {
                        val reader = InputStreamReader(this.inputStream)
                        val input = LineNumberReader(reader)
                        var line: String?
                        while (input.readLine().also { line = it } != null) {
                            logAdapter.info("${scriptFile.name} >>>> $line")
                        }
                        this.waitFor()
                        input.close()
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
                throw ServerException("脚本【${scriptFile.canonicalPath}】不能执行，仅支持.bat/.sh/.sql文件")
            }
        }
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