package pers.acp.admin.log.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.*
import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.log.base.BaseLogEntity
import pers.acp.admin.log.constant.LogApi
import pers.acp.admin.log.constant.LogConstant
import pers.acp.admin.log.domain.LogFileDomain
import pers.acp.admin.log.domain.LogDomain
import pers.acp.admin.log.po.FileDownLoadPo
import pers.acp.admin.log.po.LogQueryPo
import pers.acp.admin.log.vo.LoginLogVo
import pers.acp.admin.permission.BaseExpression
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.task.timer.Calculation
import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.vo.ErrorVo
import java.io.File
import java.nio.charset.Charset

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(LogApi.basePath)
@Api(tags = ["日志信息"])
class LogController @Autowired
constructor(private val logAdapter: LogAdapter,
            private val logFileDomain: LogFileDomain,
            private val objectMapper: ObjectMapper,
            private val logDomain: LogDomain) : BaseController(logAdapter) {

    @ApiOperation(value = "获取各应用过去3个月的登录次数统计")
    @ApiResponses(ApiResponse(code = 400, message = "没有权限做此操作；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.sysMonitor)
    @GetMapping(value = [LogApi.loginInfo], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findLoginLog(): ResponseEntity<List<LoginLogVo>> =
            CommonTools.getNowDateTime().withTimeAtStartOfDay().minusMonths(LogConstant.LOGIN_LOG_STATISTICS_MAX_MONTH).let {
                ResponseEntity.ok(logDomain.loginStatistics(it.millis))
            }

    @ApiOperation(value = "查询路由日志列表", notes = "查询条件：客户端ip、网关ip、请求路径、路由服务id、应用名称、用户名称、开始时间、结束时间、响应状态")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.sysMonitor)
    @PostMapping(value = [LogApi.gateWayRouteLog], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryRouteLog(@RequestBody @Valid logQueryPo: LogQueryPo): ResponseEntity<Page<out BaseLogEntity>> =
            ResponseEntity.ok(logDomain.doQueryRouteLog(logQueryPo))

    @ApiOperation(value = "查询操作日志列表", notes = "查询条件：客户端ip、网关ip、请求路径、路由服务id、应用名称、用户名称、开始时间、结束时间")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.sysMonitor)
    @PostMapping(value = [LogApi.operateLog], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryOperateLog(@RequestBody @Valid logQueryPo: LogQueryPo): ResponseEntity<Page<out BaseLogEntity>> =
            ResponseEntity.ok(logDomain.doQueryOperateLog(logQueryPo))

    @ApiOperation(value = "查询登录日志列表", notes = "查询条件：客户端ip、网关ip、请求路径、路由服务id、应用名称、用户名称、开始时间、结束时间")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.sysMonitor)
    @PostMapping(value = [LogApi.loginLog], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryLoginLog(@RequestBody @Valid logQueryPo: LogQueryPo): ResponseEntity<Page<out BaseLogEntity>> =
            ResponseEntity.ok(logDomain.doQueryLoginLog(logQueryPo))

    @ApiOperation(value = "查询指定日期范围的日志备份文件", notes = "查询条件：开始日期、结束日期")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [LogApi.logFile], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryFile(@ApiParam(value = "开始日期", required = true, example = "0")
                  @NotNull(message = "开始日期不能为空")
                  @RequestParam
                  startDate: Long,
                  @ApiParam(value = "结束日期", required = true, example = "0")
                  @NotNull(message = "结束日期不能为空")
                  @RequestParam
                  endDate: Long): ResponseEntity<List<String>> =
            try {
                val start = longToDate(startDate)
                val end = longToDate(endDate)
                val nowDay = longToDate(CommonTools.getNowDateTime().toDate().time)
                if (start > end) {
                    throw ServerException("开始日期不能大于结束日期")
                }
                if (end >= nowDay) {
                    throw ServerException("结束日期必须早于当前")
                }
                ResponseEntity.ok(logFileDomain.fileList(
                        CommonTools.getDateTimeString(start, Calculation.DATE_FORMAT),
                        CommonTools.getDateTimeString(end, Calculation.DATE_FORMAT)
                ))
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
                throw ServerException(e.message)
            }

    @ApiOperation(value = "日志文件下载", notes = "下载指定的日志文件")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @GetMapping(value = [LogApi.logFile], produces = [MediaType.ALL_VALUE])
    @Throws(ServerException::class)
    fun downloadFile(request: HttpServletRequest, response: HttpServletResponse,
                     @ApiParam(value = "文件内容", required = true)
                     @NotBlank(message = "文件内容不能为空")
                     @RequestParam params: String) {
        val fileContent = objectMapper.readValue(params, FileDownLoadPo::class.java)
        if (CommonTools.isNullStr(fileContent.fileName)) {
            throw ServerException("文件名称不能为空")
        }
        val fileName = String(Base64.decode(fileContent.fileName), Charset.forName(CommonTools.getDefaultCharset())).replace("/", File.separator).replace("\\", File.separator)
        logFileDomain.doDownLoadFile(request, response, fileName)
    }

}
