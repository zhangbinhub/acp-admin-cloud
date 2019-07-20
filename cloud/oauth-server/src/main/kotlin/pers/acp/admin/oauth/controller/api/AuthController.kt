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
import pers.acp.admin.common.annotation.DuplicateSubmission
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.constant.ModuleFuncCode
import pers.acp.admin.oauth.constant.OauthApi
import pers.acp.admin.oauth.constant.AuthConfigExpression
import pers.acp.admin.common.vo.InfoVO
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
import pers.acp.spring.boot.vo.ErrorVO
import pers.acp.spring.cloud.log.LogInstance

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
@Api("权限信息")
class AuthController @Autowired
constructor(private val logInstance: LogInstance, private val menuDomain: MenuDomain, private val moduleFuncDomain: ModuleFuncDomain) : BaseController() {

    private val moduleFuncCodeList: MutableList<String> = mutableListOf()

    @ApiOperation(value = "获取模块功能编码列表")
    @PreAuthorize(AuthConfigExpression.authConfig)
    @GetMapping(value = [OauthApi.moduleFuncCodes], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun findModuleFuncCode(): ResponseEntity<List<String>> = ResponseEntity.ok(moduleFuncCodeList)

    @PostConstruct
    fun init() {
        try {
            for (field in ModuleFuncCode::class.java.declaredFields) {
                moduleFuncCodeList.add(field.get(ModuleFuncCode::class.java).toString())
            }
        } catch (e: Exception) {
            logInstance.error(e.message, e)
        }

    }

    @ApiOperation(value = "获取当前用户所属菜单", notes = "根据当前登录的用户信息，查询有权访问的菜单列表")
    @GetMapping(value = [OauthApi.currMenu], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun currMenuList(user: OAuth2Authentication): ResponseEntity<List<Menu>> =
            ResponseEntity.ok(menuDomain.getMenuList(user.oAuth2Request.clientId, user.name))

    @ApiOperation(value = "获取指定应用下的菜单列表", notes = "查询指定应用的菜单列表，供选择配置")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.menuList + "/{appId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun menuList(@PathVariable appId: String): ResponseEntity<List<Menu>> {
        if (CommonTools.isNullStr(appId)) {
            throw ServerException("应用ID不能为空")
        }
        return ResponseEntity.ok(menuDomain.getMenuListByAppId(appId))
    }

    @ApiOperation(value = "获取指定应用下的模块功能列表", notes = "查询指定应用的模块功能列表，供选择配置")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.moduleFuncList + "/{appId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun moduleFuncList(@PathVariable appId: String): ResponseEntity<List<ModuleFunc>> {
        if (CommonTools.isNullStr(appId)) {
            throw ServerException("应用ID不能为空")
        }
        return ResponseEntity.ok(moduleFuncDomain.getModuleFuncListByAppId(appId))
    }

    @ApiOperation(value = "获取菜单列表", notes = "查询所有菜单列表")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun allMenuList(): ResponseEntity<List<Menu>> = ResponseEntity.ok(menuDomain.getAllMenuList())

    @ApiOperation(value = "获取模块功能列表", notes = "查询所有模块功能列表")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun allModuleFuncList(): ResponseEntity<List<ModuleFunc>> = ResponseEntity.ok(moduleFuncDomain.getAllModuleFuncList())

    @ApiOperation(value = "新建菜单信息", notes = "名称、应用ID、图标、链接、上级、序号、模式、状态、关联角色")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Menu::class), ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authAdd)
    @PutMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    fun addMenu(@RequestBody @Valid menuPO: MenuPo): ResponseEntity<Menu> = ResponseEntity.status(HttpStatus.CREATED).body(menuDomain.doCreate(menuPO))

    @ApiOperation(value = "新建模块功能信息", notes = "名称、应用ID、编码、上级、关联角色")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Menu::class), ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authAdd)
    @PutMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    fun addModuleFunc(@RequestBody @Valid moduleFuncPO: ModuleFuncPo): ResponseEntity<ModuleFunc> =
            ResponseEntity.status(HttpStatus.CREATED).body(moduleFuncDomain.doCreate(moduleFuncPO))

    @ApiOperation(value = "删除指定的菜单信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；存在下级，不允许删除；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authDelete)
    @DeleteMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun deleteMenu(@ApiParam(value = "id列表", required = true)
                   @NotEmpty(message = "id不能为空")
                   @NotNull(message = "id不能为空")
                   @RequestBody
                   idList: MutableList<String>): ResponseEntity<InfoVO> =
            menuDomain.doDelete(idList).let { ResponseEntity.ok(InfoVO(message = "删除成功")) }

    @ApiOperation(value = "删除指定的模块功能信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；存在下级，不允许删除；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authDelete)
    @DeleteMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun deleteModuleFunc(@ApiParam(value = "id列表", required = true)
                         @NotEmpty(message = "id不能为空")
                         @NotNull(message = "id不能为空")
                         @RequestBody
                         idList: MutableList<String>): ResponseEntity<InfoVO> =
            moduleFuncDomain.doDelete(idList).let { ResponseEntity.ok(InfoVO(message = "删除成功")) }

    @ApiOperation(value = "更新菜单信息", notes = "名称、应用ID、图标、链接、上级、序号、模式、状态、关联角色")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authUpdate)
    @PatchMapping(value = [OauthApi.menuConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun updateMenu(@RequestBody @Valid menuPO: MenuPo): ResponseEntity<Menu> =
            ResponseEntity.ok(menuDomain.doUpdate(menuPO))

    @ApiOperation(value = "更新模块功能信息", notes = "名称、应用ID、编码、上级、关联角色")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；模块功能编码非法，请重新输入；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authUpdate)
    @PatchMapping(value = [OauthApi.moduleFuncConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun updateModuleFunc(@RequestBody @Valid moduleFuncPO: ModuleFuncPo): ResponseEntity<ModuleFunc> {
        if (!moduleFuncCodeList.contains(moduleFuncPO.code)) {
            throw ServerException("模块功能编码非法，请重新输入")
        }
        return ResponseEntity.ok(moduleFuncDomain.doUpdate(moduleFuncPO))
    }

    @ApiOperation(value = "获取菜单详细信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.menuConfig + "/{menuId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun menuInfo(@ApiParam(value = "菜单id", required = true)
                 @NotBlank(message = "菜单id不能为空")
                 @PathVariable
                 menuId: String): ResponseEntity<MenuVo> =
            ResponseEntity.ok(menuDomain.getMenuInfo(menuId))

    @ApiOperation(value = "获取模块功能详细信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = [OauthApi.moduleFuncConfig + "/{moduleFuncId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun moduleFuncInfo(@ApiParam(value = "模块功能id", required = true)
                       @NotBlank(message = "模块功能id不能为空")
                       @PathVariable
                       moduleFuncId: String): ResponseEntity<ModuleFuncVo> =
            ResponseEntity.ok(moduleFuncDomain.getModuleFuncInfo(moduleFuncId))

}
