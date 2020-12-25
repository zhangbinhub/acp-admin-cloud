package pers.acp.admin.oauth.component

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * 密码编码器
 *
 * @author zhangbin by 11/04/2018 17:14
 * @since JDK 11
 */
@Component
class UserPasswordEncoder : PasswordEncoder {
    /**
     * 编码
     *
     * @param rawPassword 原始密码
     * @return 编码结果
     */
    override fun encode(rawPassword: CharSequence) = rawPassword.toString()

    /**
     * 匹配
     *
     * @param rawPassword     原始密码，客户端上送的值
     * @param encodedPassword 编码后的密码，存储在服务器上的值，SecurityUserDetailsService 指定
     * @return 匹配结果
     */
    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean =
            rawPassword.toString().equals(encodedPassword, ignoreCase = true)
}
