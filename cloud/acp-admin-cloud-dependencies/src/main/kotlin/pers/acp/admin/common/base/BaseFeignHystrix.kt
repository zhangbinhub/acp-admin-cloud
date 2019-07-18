package pers.acp.admin.common.base

import com.fasterxml.jackson.databind.ObjectMapper
import feign.hystrix.FallbackFactory
import pers.acp.spring.cloud.log.LogInstance

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
abstract class BaseFeignHystrix<T> protected constructor(protected val logInstance: LogInstance, protected val objectMapper: ObjectMapper) : FallbackFactory<T>
