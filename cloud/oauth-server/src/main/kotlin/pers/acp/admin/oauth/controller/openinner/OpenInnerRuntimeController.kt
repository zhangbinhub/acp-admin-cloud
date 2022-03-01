package pers.acp.admin.oauth.controller.openinner

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.vo.ErrorVo
import io.swagger.annotations.*
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.RuntimeConfigVo
import pers.acp.admin.oauth.domain.RuntimeConfigDomain
import javax.validation.constraints.NotBlank

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.openInnerBasePath)
@Api(tags = ["运行参数配置（内部开放接口）"])
class OpenInnerRuntimeController @Autowired
constructor(
    logAdapter: LogAdapter,
    private val runtimeConfigDomain: RuntimeConfigDomain
) : BaseController(logAdapter) {

    @ApiOperation(value = "获取参数信息", notes = "根据参数名称获取")
    @ApiResponses(ApiResponse(code = 400, message = "找不到参数信息；", response = ErrorVo::class))
    @GetMapping(value = [OauthApi.runtime + "/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun find(
        @ApiParam(value = "参数名称", required = true)
        @NotBlank(message = "参数名称不能为空")
        @PathVariable
        name: String
    ): ResponseEntity<RuntimeConfigVo> =
        (runtimeConfigDomain.findByName(name) ?: throw ServerException("找不到参数信息")).let {
            RuntimeConfigVo().apply {
                BeanUtils.copyProperties(it, this)
            }.let {
                ResponseEntity.ok(it)
            }
        }

    @ApiOperation(value = "获取参数信息", notes = "根据参数名称获取")
    @GetMapping(value = [OauthApi.runtime], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findList(): ResponseEntity<Map<String, RuntimeConfigVo>> = ResponseEntity.ok(runtimeConfigDomain.findAll())

}
