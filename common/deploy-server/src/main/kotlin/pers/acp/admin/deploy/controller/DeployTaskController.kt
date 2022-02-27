package pers.acp.admin.deploy.controller

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.vo.ErrorVo
import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudDuplicateSubmission
import io.github.zhangbinhub.acp.core.CommonTools
import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.deploy.bus.publish.DeployEventPublish
import pers.acp.admin.deploy.constant.DeployApi
import pers.acp.admin.deploy.domain.DeployTaskDomain
import pers.acp.admin.deploy.entity.DeployTask
import pers.acp.admin.deploy.po.DeployTaskPo
import pers.acp.admin.deploy.po.DeployTaskQueryPo
import pers.acp.admin.permission.BaseExpression
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Validated
@RestController
@RequestMapping(DeployApi.basePath)
@Api(tags = ["部署任务管理"])
class DeployTaskController @Autowired
constructor(
    logAdapter: LogAdapter,
    private val deployTaskDomain: DeployTaskDomain,
    private val deployEventPublish: DeployEventPublish
) : BaseController(logAdapter) {
    @ApiOperation(value = "新建部署任务")
    @ApiResponses(
        ApiResponse(code = 201, message = "创建成功", response = DeployTask::class),
        ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class)
    )
    @PreAuthorize(BaseExpression.superOnly)
    @PutMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun add(@RequestBody @Valid deployTaskPo: DeployTaskPo): ResponseEntity<DeployTask> =
        deployTaskDomain.doCreate(deployTaskPo).let {
            ResponseEntity.status(HttpStatus.CREATED).body(it)
        }

    @ApiOperation(value = "删除指定的部署任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @DeleteMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun delete(
        @ApiParam(value = "id列表", required = true)
        @NotEmpty(message = "id不能为空")
        @NotNull(message = "id不能为空")
        @RequestBody
        idList: MutableList<String>
    ): ResponseEntity<InfoVo> =
        deployTaskDomain.doDelete(idList).let {
            ResponseEntity.ok(InfoVo(message = "删除成功"))
        }

    @ApiOperation(value = "更新指定的部署任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PatchMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(@RequestBody @Valid deployTaskPo: DeployTaskPo): ResponseEntity<DeployTask> {
        if (CommonTools.isNullStr(deployTaskPo.id)) {
            throw ServerException("ID不能为空")
        }
        return deployTaskDomain.doUpdate(deployTaskPo).let {
            ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "查询部署任务列表")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody @Valid deployTaskQueryPo: DeployTaskQueryPo): ResponseEntity<Page<DeployTask>> =
        deployTaskQueryPo.let {
            if (it.startTime != null) {
                it.startTime = longToDate(it.startTime!!).millis
            }
            if (it.endTime != null) {
                it.endTime = longToDate(it.endTime!!).millis
            }
            ResponseEntity.ok(deployTaskDomain.doQuery(deployTaskQueryPo))
        }

    @ApiOperation(value = "执行部署任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [DeployApi.taskExecute + "/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun execute(@PathVariable id: String): ResponseEntity<InfoVo> =
        deployTaskDomain.executeTask(id).let { deployTask ->
            deployEventPublish.doNotifyExecuteDeploy(deployTask.id).let {
                ResponseEntity.accepted().body(InfoVo(message = "请求成功，稍后将执行部署任务"))
            }
        }
}