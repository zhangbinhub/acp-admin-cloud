package pers.acp.admin.oauth.controller.api

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.oauth.constant.OauthApi
import pers.acp.admin.permission.BaseExpression
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.oauth.domain.ApplicationDomain
import pers.acp.admin.oauth.domain.UserDomain
import pers.acp.admin.oauth.token.SecurityTokenService
import pers.acp.admin.oauth.vo.OnlineInfoVo
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("登录信息")
class LoginController @Autowired
constructor(private val logAdapter: LogAdapter,
            private val applicationDomain: ApplicationDomain,
            private val userDomain: UserDomain,
            private val securityTokenService: SecurityTokenService) : BaseController() {

    @ApiOperation(value = "注销当前用户")
    @PostMapping(value = [OauthApi.logOut], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun doLogOut(user: OAuth2Authentication): ResponseEntity<InfoVo> =
            try {
                securityTokenService.removeToken(user)
                logAdapter.info("用户[loginNo=" + user.name + "]主动下线!")
                ResponseEntity.ok(InfoVo(message = "成功下线"))
            } catch (e: Exception) {
                throw ServerException(e.message)
            }

    @ApiOperation(value = "获取各应用在线用户数统计")
    @GetMapping(value = [OauthApi.onlineInfo], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun getOnlineInfo(user: OAuth2Authentication): ResponseEntity<List<OnlineInfoVo>> =
            try {
                mutableListOf<OnlineInfoVo>().apply {
                    applicationDomain.getAppList(user).forEach {
                        this.add(OnlineInfoVo(
                                appId = it.id,
                                appName = it.appName,
                                count = securityTokenService.getTokensByAppId(it.id).size.toLong()
                        ))
                    }
                }.let {
                    ResponseEntity.ok(it)
                }
            } catch (e: Exception) {
                throw ServerException(e.message)
            }

    @ApiOperation(value = "获取用户在线情况")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过，找不到用户信息；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @GetMapping(value = [OauthApi.onlineInfo + "/{userId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun getOnlineInfo(user: OAuth2Authentication,
                      @ApiParam(value = "用户id", required = true)
                      @PathVariable
                      userId: String): ResponseEntity<List<OnlineInfoVo>> =
            try {
                mutableListOf<OnlineInfoVo>().apply {
                    val userInfo = userDomain.getUserInfo(userId) ?: throw ServerException("找不到该用户信息")
                    applicationDomain.getAppList(user).forEach { item ->
                        this.add(OnlineInfoVo(
                                appId = item.id,
                                appName = item.appName,
                                count = securityTokenService.getTokensByAppIdAndLoginNo(item.id, userInfo.loginNo).size.toLong()
                        ))
                    }
                }.let {
                    ResponseEntity.ok(it)
                }
            } catch (e: Exception) {
                throw ServerException(e.message)
            }

    @ApiOperation(value = "指定应用下的用户强制下线")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @DeleteMapping(value = [OauthApi.onlineInfo + "/{appId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun delete(@ApiParam(value = "应用id", required = true)
               @PathVariable
               appId: String,
               @ApiParam(value = "用户id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: List<String>): ResponseEntity<InfoVo> =
            try {
                idList.forEach {
                    val userInfo = userDomain.getUserInfo(it) ?: throw ServerException("找不到该用户信息")
                    securityTokenService.removeTokensByAppIdAndLoginNo(appId, userInfo.loginNo)
                    logAdapter.info("用户[" + userInfo.name + "(" + userInfo.loginNo + ")]被管理员强制下线!")
                }
                ResponseEntity.ok(InfoVo(message = "成功下线"))
            } catch (e: Exception) {
                throw ServerException(e.message)
            }

}
