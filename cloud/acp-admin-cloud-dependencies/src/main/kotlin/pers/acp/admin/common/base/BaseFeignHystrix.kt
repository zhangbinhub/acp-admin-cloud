package pers.acp.admin.common.base

import com.fasterxml.jackson.databind.ObjectMapper
import feign.hystrix.FallbackFactory
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
abstract class BaseFeignHystrix<T> protected constructor(protected val logAdapter: LogAdapter, protected val objectMapper: ObjectMapper) : FallbackFactory<T>
