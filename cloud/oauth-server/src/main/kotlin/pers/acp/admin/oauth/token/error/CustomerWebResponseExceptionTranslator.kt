package pers.acp.admin.oauth.token.error

import pers.acp.spring.boot.enums.ResponseCode
import pers.acp.spring.boot.exceptions.ServerException
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class CustomerWebResponseExceptionTranslator : WebResponseExceptionTranslator<OAuth2Exception> {
    override fun translate(e: Exception): ResponseEntity<OAuth2Exception> =
        when (e) {
            is CustomerOAuth2Exception -> {
                e
            }
            is InvalidGrantException -> {
                CustomerOAuth2Exception(
                    e.cause?.message ?: e.message ?: ResponseCode.AuthError.description,
                    ServerException("用户名或密码不正确！")
                )
            }
            else -> {
                CustomerOAuth2Exception(
                    e.cause?.message ?: e.message ?: ResponseCode.AuthError.description,
                    e.cause ?: e
                )
            }
        }.let {
            ResponseEntity.badRequest().body(it)
        }
}