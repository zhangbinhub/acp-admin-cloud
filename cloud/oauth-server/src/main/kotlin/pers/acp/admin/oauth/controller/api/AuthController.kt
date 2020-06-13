package pers.acp.admin.oauth.controller.api

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.vo.BooleanInfoVo
import pers.acp.admin.oauth.constant.AuthConfigExpression
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.oauth.domain.MenuDomain
import pers.acp.admin.oauth.domain.ModuleFuncDomain
import pers.acp.admin.oauth.entity.Menu
import pers.acp.admin.oauth.entity.ModuleFunc
import pers.acp.admin.oauth.po.MenuPo
import pers.acp.admin.oauth.po.ModuleFuncPo
import pers.acp.admin.oauth.vo.MenuVo
import pers.acp.admin.oauth.vo.ModuleFuncVo
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission

import javax.annotation.PostConstruct
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api(tags = ["权限信息"])
class AuthController @Autowired
constructor(private val logAdapter: LogAdapter,
            private val menuDomain: MenuDomain,
            private val moduleFuncDomain: ModuleFuncDomain) : BaseController(logAdapter) {

    private val moduleFuncCodeList: MutableList<String> = mutableListOf()

    @ApiOperation(value = "获取模块功能编码列表")
    @PreAuthorize(AuthConfigExpression.authConfig)
    @GetMapping(value = [OauthApi.moduleFuncCodes], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findModuleFuncCode(): ResponseEntity<List<String>> = ResponseEntity.ok(moduleFuncCodeList)

    @PostConstruct
    fun init() {
        try {
            for (field in ModuleFuncCode::class.java.declaredFields) {
                val value = field.get(ModuleFuncCode::class.java)
                if (value is String) {
                    moduleFuncCodeList.add(value)
                }
            }
            moduleFuncCodeList.sort()
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

    }

    @ApiOperation(value = "判断当前用户是否具有指定的权限")
    @GetMapping(value = [OauthApi.authentication + "/{authentication}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currUserHasAuthentication(user: OAuth2Authentication, @PathVariable authentication: String): ResponseEntity<BooleanInfoVo> =
            ResponseEntity.ok(BooleanInfoVo(result = hasAuthentication(user, mutableListOf(authentication))))

    @ApiOperation(value = "获取当前用户所属菜单", notes = "根据当前登录的用户信息，查询有权访问的菜单列表")
    @GetMapping(value = [OauthApi.currMenu], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currMenuList(user: OAuth2Authentication): ResponseEntity<List<Menu>> =
            ResponseEntity.ok(menuDomain.getMenuList(user.oAuth2Request.clientId, user.name))

    @ApiOperation(value = "获取当前用户所有功能权限信息", notes = "根据当前登录的用户信息，查询具备的功能权限")
    @GetMapping(value = [OauthApi.currModuleFunc], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currModuleFuncList(user: OAuth2Authentication): ResponseEntity<List<ModuleFunc>> =
            ResponseEntity.ok(moduleFuncDomain.getModuleFuncList(user.oAuth2Request.clientId, user.name))

    @ApiOperation(value = "获取指定应用下的菜单列表", notes = "查询指定应用的菜单列表，供选择配置")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.menuList + "/{appId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun menuList(@PathVariable appId: String): ResponseEntity<List<Menu>> =
            ResponseEntity.ok(menuDomain.getMenuListByAppId(appId))

    @ApiOperation(value = "获取指定应用下的模块功能列表", notes = "查询指定应用的模块功能列表，供选择配置")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.moduleFuncList + "/{appId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun moduleFuncList(@PathVariable appId: String): ResponseEntity<List<ModuleFunc>> =
            ResponseEntity.ok(moduleFuncDomain.getModuleFuncListByAppId(appId))

    @ApiOperation(value = "获取菜单列表", notes = "查询所有菜单列表")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun allMenuList(): ResponseEntity<List<Menu>> = ResponseEntity.ok(menuDomain.getAllMenuList())

    @ApiOperation(value = "获取模块功能列表", notes = "查询所有模块功能列表")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun allModuleFuncList(): ResponseEntity<List<ModuleFunc>> = ResponseEntity.ok(moduleFuncDomain.getAllModuleFuncList())

    @ApiOperation(value = "新建菜单信息", notes = "名称、应用ID、图标、链接、上级、序号、模式、状态、关联角色")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Menu::class), ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authAdd)
    @PutMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    fun addMenu(user: OAuth2Authentication, @RequestBody @Valid menuPo: MenuPo): ResponseEntity<Menu> =
            menuDomain.doCreate(user, menuPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "新建模块功能信息", notes = "名称、应用ID、编码、上级、关联角色")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Menu::class), ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authAdd)
    @PutMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    fun addModuleFunc(user: OAuth2Authentication, @RequestBody @Valid moduleFuncPo: ModuleFuncPo): ResponseEntity<ModuleFunc> =
            moduleFuncDomain.doCreate(user, moduleFuncPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除指定的菜单信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；存在下级，不允许删除；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authDelete)
    @DeleteMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun deleteMenu(@ApiParam(value = "id列表", required = true)
                   @NotEmpty(message = "id不能为空")
                   @NotNull(message = "id不能为空")
                   @RequestBody
                   idList: MutableList<String>): ResponseEntity<InfoVo> =
            menuDomain.doDelete(idList).let { ResponseEntity.ok(InfoVo(message = "删除成功")) }

    @ApiOperation(value = "删除指定的模块功能信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；存在下级，不允许删除；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authDelete)
    @DeleteMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun deleteModuleFunc(@ApiParam(value = "id列表", required = true)
                         @NotEmpty(message = "id不能为空")
                         @NotNull(message = "id不能为空")
                         @RequestBody
                         idList: MutableList<String>): ResponseEntity<InfoVo> =
            moduleFuncDomain.doDelete(idList).let { ResponseEntity.ok(InfoVo(message = "删除成功")) }

    @ApiOperation(value = "更新菜单信息", notes = "名称、应用ID、图标、链接、上级、序号、模式、状态、关联角色")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authUpdate)
    @PatchMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun updateMenu(user: OAuth2Authentication, @RequestBody @Valid menuPo: MenuPo): ResponseEntity<Menu> {
        if (CommonTools.isNullStr(menuPo.id)) {
            throw ServerException("配置ID不能为空")
        }
        return ResponseEntity.ok(menuDomain.doUpdate(user, menuPo))
    }

    @ApiOperation(value = "更新模块功能信息", notes = "名称、应用ID、编码、上级、关联角色")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；模块功能编码非法，请重新输入；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authUpdate)
    @PatchMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun updateModuleFunc(user: OAuth2Authentication, @RequestBody @Valid moduleFuncPo: ModuleFuncPo): ResponseEntity<ModuleFunc> {
        if (CommonTools.isNullStr(moduleFuncPo.id)) {
            throw ServerException("配置ID不能为空")
        }
        return ResponseEntity.ok(moduleFuncDomain.doUpdate(user, moduleFuncPo))
    }

    @ApiOperation(value = "获取菜单详细信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.menuConfig + "/{menuId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun menuInfo(@ApiParam(value = "菜单id", required = true)
                 @NotBlank(message = "菜单id不能为空")
                 @PathVariable
                 menuId: String): ResponseEntity<MenuVo> =
            ResponseEntity.ok(menuDomain.getMenuInfo(menuId))

    @ApiOperation(value = "获取模块功能详细信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.moduleFuncConfig + "/{moduleFuncId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun moduleFuncInfo(@ApiParam(value = "模块功能id", required = true)
                       @NotBlank(message = "模块功能id不能为空")
                       @PathVariable
                       moduleFuncId: String): ResponseEntity<ModuleFuncVo> =
            ResponseEntity.ok(moduleFuncDomain.getModuleFuncInfo(moduleFuncId))

}
