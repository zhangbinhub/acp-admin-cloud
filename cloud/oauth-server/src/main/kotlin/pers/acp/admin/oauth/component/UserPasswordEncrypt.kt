package pers.acp.admin.oauth.component

import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.security.Sha256Encrypt
import org.springframework.stereotype.Component

/**
 * 密码加解密工具类
 */
@Component
class UserPasswordEncrypt {
    /**
     * 密码加密（用于存储）
     * @param loginNo 登录账号
     * @param password 密码明文
     * @return 加密后的存储密文
     */
    fun encrypt(loginNo: String, password: String) = Sha256Encrypt.encrypt(Sha256Encrypt.encrypt(password) + loginNo)

    /**
     * 密码加密（用于登录）
     * @param encryptedPassword 加密后的密码存储密文
     * @return 加密后的验证密文
     */
    fun encryptForLogin(encryptedPassword: String) =
        encryptForLogin(encryptedPassword, CommonTools.getDateTimeString(dateTimeFormat = "yyyyMMddHH"))

    /**
     * 密码登录验证
     * @param rawPassword 用户输入的密码
     * @param encryptedPassword 加密后的密码存储密文
     * @return true|false
     */
    fun matches(rawPassword: String, encryptedPassword: String): Boolean {
        CommonTools.getNowDateTime().let { now ->
            for (o in -offset..offset) {
                val password = encryptForLogin(
                    encryptedPassword,
                    CommonTools.getDateTimeString(now.plusHours(o), "yyyyMMddHH")
                )
                if (rawPassword.equals(password, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }

    /**
     * 密码加密（用于登录）
     * @param encryptedPassword 加密后的密码存储密文
     * @param factor 加密因子
     * @return 加密后的验证密文
     */
    private fun encryptForLogin(encryptedPassword: String, factor: String) =
        Sha256Encrypt.encrypt(encryptedPassword + factor)

    companion object {
        private const val offset = 1
    }
}