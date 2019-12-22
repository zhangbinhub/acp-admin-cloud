package pers.acp.admin.workflow.domain

import org.flowable.bpmn.converter.BpmnXMLConverter
import org.flowable.bpmn.model.Process
import org.flowable.engine.ProcessEngine
import org.flowable.engine.RepositoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.workflow.po.WorkFlowDefinitionPo
import pers.acp.admin.workflow.po.WorkFlowDefinitionQueryPo
import pers.acp.admin.workflow.constant.WorkFlowConstant
import pers.acp.admin.workflow.entity.WorkFlowDefinition
import pers.acp.admin.workflow.repo.WorkFlowDefinitionRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.component.FileDownLoadHandle
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset
import javax.persistence.criteria.Predicate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class WorkFlowDefinitionDomain @Autowired
constructor(private val logAdapter: LogAdapter,
            private val repositoryService: RepositoryService,
            private val workFlowDefinitionRepository: WorkFlowDefinitionRepository,
            private val fileDownLoadHandle: FileDownLoadHandle,
            @param:Qualifier("processEngine") private val processEngine: ProcessEngine) : BaseDomain() {

    private fun parseProcessDefinition(processFile: File): Process {
        var reader: XMLStreamReader? = null
        try {
            reader = XMLInputFactory.newInstance().createXMLStreamReader(FileInputStream(processFile))
            val model = BpmnXMLConverter().convertToBpmnModel(reader)
            val processes = model.processes
            if (processes.isEmpty()) {
                throw ServerException("没有配置流程信息")
            }
            return processes[0]!!.also {
                if (CommonTools.isNullStr(it.id)) {
                    throw ServerException("没有配置流程id")
                }
                if (CommonTools.isNullStr(it.name)) {
                    throw ServerException("没有配置流程名称")
                }
            }
        } catch (e: Exception) {
            throw ServerException(e.message)
        } finally {
            reader?.close()
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doDeploy(id: String): WorkFlowDefinition =
            workFlowDefinitionRepository.save(workFlowDefinitionRepository.getOne(id).apply {
                val deployment = repositoryService.createDeployment()
                        .name(name)
                        .key(processKey)
                        .addInputStream(WorkFlowConstant.baseResourcePath + "/" + resourceName,
                                ByteArrayInputStream(content.toByteArray(Charset.forName(CommonTools.getDefaultCharset()))))
                        .deploy() ?: throw ServerException("流程部署失败！")
                deployTime = deployment.deploymentTime.time
                deploymentId = deployment.id
            })

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(workFlowDefinitionPo: WorkFlowDefinitionPo, file: MultipartFile): WorkFlowDefinition {
        val resourceName = file.originalFilename!!
        val tmpFile = File(WorkFlowConstant.upLoadTempPath + "/" + System.currentTimeMillis() + "_" + resourceName)
        val fold = tmpFile.parentFile
        if (!fold.exists() && !fold.mkdirs()) {
            throw ServerException("创建路径失败：" + fold.absolutePath)
        }
        file.transferTo(tmpFile.absoluteFile)
        val process = parseProcessDefinition(tmpFile)
        val content = CommonTools.getFileContent(tmpFile.canonicalPath) ?: throw ServerException("流程配置文件内容为空")
        val version = workFlowDefinitionRepository.findAllByProcessKeyOrderByVersionDesc(process.id)
                .size + 1
        return WorkFlowDefinition(
                processKey = process.id,
                name = process.name,
                version = version,
                remarks = workFlowDefinitionPo.remarks,
                resourceName = resourceName,
                content = content
        ).let {
            CommonTools.doDeleteFile(tmpFile)
            workFlowDefinitionRepository.save(it)
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(workFlowDefinitionPo: WorkFlowDefinitionPo): WorkFlowDefinition =
            workFlowDefinitionRepository.save(workFlowDefinitionRepository.getOne(workFlowDefinitionPo.id!!).apply {
                remarks = workFlowDefinitionPo.remarks
                modifyTime = System.currentTimeMillis()
            })

    @Transactional
    fun doDelete(idList: MutableList<String>) = workFlowDefinitionRepository.deleteByIdIn(idList)

    fun doQuery(workFlowDefinitionQueryPo: WorkFlowDefinitionQueryPo): Page<WorkFlowDefinition> =
            workFlowDefinitionRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList: MutableList<Predicate> = mutableListOf()
                if (!CommonTools.isNullStr(workFlowDefinitionQueryPo.resourceName)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("resourceName").`as`(String::class.java), "%" + workFlowDefinitionQueryPo.resourceName + "%"))
                }
                if (!CommonTools.isNullStr(workFlowDefinitionQueryPo.name)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("name").`as`(String::class.java), "%" + workFlowDefinitionQueryPo.name + "%"))
                }
                if (!CommonTools.isNullStr(workFlowDefinitionQueryPo.processKey)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("processKey").`as`(String::class.java), "%" + workFlowDefinitionQueryPo.processKey + "%"))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(workFlowDefinitionQueryPo.queryParam!!))

    @Throws(ServerException::class)
    fun doDownLoadFile(request: HttpServletRequest, response: HttpServletResponse, id: String) {
        val workFlowDefinition = workFlowDefinitionRepository.getOne(id)
        val targetFile = CommonTools.contentWriteToFile(WorkFlowConstant.upLoadTempPath + "/" + System.currentTimeMillis() + "_" + workFlowDefinition.resourceName,
                workFlowDefinitionRepository.getOne(id).content) ?: throw ServerException("生成文件失败")
        val foldPath = targetFile.parentFile.canonicalPath
        fileDownLoadHandle.downLoadFile(request, response, targetFile.canonicalPath, listOf("$foldPath/.*"), true, 120000)
    }

    /**
     * 生成定义流程图
     *
     * @param deploymentId 流程部署ID
     * @param imgType 图片格式
     * @return 流程图输入流
     * @throws ServerException 异常
     */
    @Throws(ServerException::class)
    fun generateDefinitionDiagram(deploymentId: String, imgType: String): InputStream =
            try {
                repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult().let {
                    val model = repositoryService.getBpmnModel(it.id)
                    val engineConfiguration = processEngine.processEngineConfiguration
                    val diagramGenerator = engineConfiguration.processDiagramGenerator
                    diagramGenerator.generateDiagram(model, imgType.toLowerCase(), listOf(), listOf(),
                            engineConfiguration.activityFontName, engineConfiguration.labelFontName, engineConfiguration.annotationFontName,
                            engineConfiguration.classLoader, 1.0, true)
                }
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
                throw ServerException(e.message)
            }

}
