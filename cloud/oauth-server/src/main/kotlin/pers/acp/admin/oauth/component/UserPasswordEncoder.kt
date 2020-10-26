package pers.acp.admin.oauth.component

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pers.acp.core.CommonTools
import pers.acp.core.security.Sha256Encrypt

/**
 * 密码编码器
 *
 * @author zhangbin by 11/04/2018 17:14
 * @since JDK 11
 */
@Component
class UserPasswordEncoder : PasswordEncoder {

    /**
     * 前后偏移量
     */
    private val offset = 1

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
            if (encodedPassword.length <= 32) {
                // client secret or other
                rawPassword.toString().equals(encodedPassword, ignoreCase = true)
            } else {
                // user password
                CommonTools.getNowDateTime().let { now ->
                    for (o in -offset..offset) {
                        val password = Sha256Encrypt.encrypt(encodedPassword + CommonTools.getDateTimeString(now.plusHours(o), "yyyyMMddHH"))
                        if (rawPassword.toString().equals(password, ignoreCase = true)) {
                            return true
                        }
                    }
                    false
                }
            }
}
