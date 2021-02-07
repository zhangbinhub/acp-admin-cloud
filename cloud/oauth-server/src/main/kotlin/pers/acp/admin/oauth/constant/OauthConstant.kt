package pers.acp.admin.oauth.constant

object OauthConstant {
    /**
     * 自定义认证方式：用户密码
     */
    const val granterUserPassword = "user_password"

    /**
     * 密码连续错误限制次数KEY前缀
     */
    const val passwordErrorTimeKeyPrefix = "OAUTH_PASSWORD_ERROR_TIME_"

    /**
     * 密码连续错误限制次数
     */
    const val passwordErrorTime = "PASSWORD_ERROR_TIME"

    /**
     * 修改密码间隔时间，单位：毫秒
     */
    const val passwordUpdateIntervalTime = "PASSWORD_UPDATE_INTERVAL_TIME"
}