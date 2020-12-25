package pers.acp.admin.oauth.token.error

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.security.oauth2.common.exceptions.*

@JsonSerialize(using = CustomerOAuth2ExceptionSerializer::class)
class CustomerOAuth2Exception : OAuth2Exception {
    constructor(msg: String?) : super(msg)
    constructor(msg: String?, t: Throwable?) : super(msg, t)
}