package pers.acp.admin.oauth.domain.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.core.CommonTools
import pers.acp.core.security.Sha256Encrypt
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhangbin by 11/04/2018 15:19
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class SecurityUserDetailsDomain @Autowired
constructor(private val logAdapter: LogAdapter, private val userRepository: UserRepository) : UserDetailsService {

    /**
     * 根据 username 获取用户信息
     *
     * @param username 用户名
     * @return 用户对象
     * @throws UsernameNotFoundException 找不到用户信息异常
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByLoginNo(username).orElse(null)
        if (user == null) {
            logAdapter.error("无此用户：$username")
            throw UsernameNotFoundException("无此用户：$username")
        }
        val grantedAuthorities: MutableSet<GrantedAuthority> = mutableSetOf()
        user.roleSet.forEach { role ->
            grantedAuthorities.add(SimpleGrantedAuthority(RoleCode.prefix + role.code)) //角色编码
            role.moduleFuncSet.forEach { module ->
                grantedAuthorities.add(SimpleGrantedAuthority(module.code)) //模块功能编码
            }
        }
        return User(user.loginNo,
                Sha256Encrypt.encrypt(user.password + CommonTools.getDateTimeString(null, "yyyyMMddHH")),
                user.enabled, true, true, true,
                grantedAuthorities)
    }

}
