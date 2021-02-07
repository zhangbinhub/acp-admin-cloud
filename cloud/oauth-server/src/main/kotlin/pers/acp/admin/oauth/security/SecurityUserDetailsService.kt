package pers.acp.admin.oauth.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.oauth.constant.OauthConstant
import pers.acp.admin.oauth.domain.ModuleFuncDomain
import pers.acp.admin.oauth.domain.RuntimeConfigDomain
import pers.acp.admin.oauth.domain.UserDomain
import pers.acp.admin.oauth.token.error.CustomerOAuth2Exception
import pers.acp.core.CommonTools
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhangbin by 11/04/2018 15:19
 * @since JDK 11
 */
@Component
class SecurityUserDetailsService @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val userDomain: UserDomain,
    private val moduleFuncDomain: ModuleFuncDomain,
    private val runtimeConfigDomain: RuntimeConfigDomain
) : UserDetailsService {

    /**
     * 根据 username 获取用户信息
     *
     * @param username 用户名
     * @return 用户对象
     * @throws UsernameNotFoundException 找不到用户信息异常
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails =
        userDomain.getUserInfoByLoginNo(username, true).let { user ->
            if (user == null) {
                logAdapter.error("无此用户：$username")
                throw UsernameNotFoundException("无此用户：$username")
            }
            val grantedAuthorities: MutableSet<GrantedAuthority> = mutableSetOf()
            user.roleSet.forEach { role ->
                grantedAuthorities.add(SimpleGrantedAuthority(RoleCode.prefix + role.code)) //角色编码
            }
            moduleFuncDomain.getModuleFuncList(user.id).forEach { module ->
                grantedAuthorities.add(SimpleGrantedAuthority(module.code)) //模块功能编码
            }
            User(user.loginNo, user.password, user.enabled, true, true, true, grantedAuthorities)
        }

    /**
     * 记录用户密码错误次数
     */
    @Throws(CustomerOAuth2Exception::class)
    fun storePasswordErrorTime(username: String) = userDomain.storePasswordErrorTime(username).let {
        runtimeConfigDomain.findByName(OauthConstant.passwordErrorTime)?.let { runtimeConfig ->
            if (runtimeConfig.enabled && !CommonTools.isNullStr(runtimeConfig.value)) {
                runtimeConfig.value!!.toInt().let { maxPasswordErrorTime ->
                    if (maxPasswordErrorTime in 1..it) {
                        userDomain.getUserInfoByLoginNo(username)?.apply {
                            this.enabled = false
                            userDomain.doSaveUser(this)
                        }
                        throw CustomerOAuth2Exception("错误次数已达${maxPasswordErrorTime}次，请联系系统管理员！")
                    }
                }
            }
        }
    }

    /**
     * 清除用户密码错误次数
     */
    fun clearPasswordErrorTime(username: String) = userDomain.clearPasswordErrorTime(username)
}
