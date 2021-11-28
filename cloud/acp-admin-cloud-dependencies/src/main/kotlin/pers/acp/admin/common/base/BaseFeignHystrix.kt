package pers.acp.admin.common.base

import com.fasterxml.jackson.databind.ObjectMapper
import feign.FeignException
import org.springframework.cloud.openfeign.FallbackFactory
import pers.acp.core.CommonTools
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import java.nio.charset.Charset

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
abstract class BaseFeignHystrix<T> protected constructor(
    protected val logAdapter: LogAdapter,
    private val objectMapper: ObjectMapper
) : FallbackFactory<T> {
    fun getErrorMessage(cause: Throwable?) = cause?.let { throwable ->
        if (throwable is FeignException.BadRequest) {
            throwable.responseBody().let { optional ->
                if (optional.isPresent) {
                    try {
                        objectMapper.readValue(
                            Charset.forName(CommonTools.getDefaultCharset()).decode(optional.get()).toString(),
                            ErrorVo::class.java
                        ).errorDescription
                    } catch (e: Exception) {
                        logAdapter.error(e.message, e)
                        "服务异常"
                    }
                } else {
                    "服务异常"
                }
            }
        } else {
            "服务异常"
        }
    }
}
