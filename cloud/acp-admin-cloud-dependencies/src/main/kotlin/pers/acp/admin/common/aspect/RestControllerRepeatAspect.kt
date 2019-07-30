package pers.acp.admin.common.aspect

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.Order
import pers.acp.admin.common.annotation.DuplicateSubmission
import pers.acp.admin.common.lock.DistributedLock
import pers.acp.core.security.MD5Utils
import pers.acp.spring.boot.exceptions.ServerException

/**
 * controller拦截器
 *
 * @author zhangbin by 21/11/2017 10:06
 * @since JDK 11
 */
@Aspect
@Order(0)
class RestControllerRepeatAspect(private val distributedLock: DistributedLock, private val objectMapper: ObjectMapper) {

    /**
     * 定义拦截规则
     */
    @Pointcut(value = "execution(public * *(..)) && @annotation(pers.acp.admin.common.annotation.DuplicateSubmission)")
    fun executeService() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp 拦截对象
     * @return Object（被拦截方法的执行结果）
     */
    @Around("executeService()")
    @Throws(Throwable::class)
    fun doAround(pjp: ProceedingJoinPoint): Any {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val duplicateSubmission = method.getAnnotation(DuplicateSubmission::class.java)
        val key = getKey(signature.declaringTypeName + "." + method.name, duplicateSubmission.keyExpress, pjp.args)
        val expire = duplicateSubmission.expire
        try {
            val response: Any
            if (distributedLock.getLock(key, key, expire)) {
                response = pjp.proceed()
                distributedLock.releaseLock(key, key)
            } else {
                throw ServerException("请勿重复请求")
            }
            return response
        } catch (e: Exception) {
            throw ServerException(e.message)
        }

    }

    private fun getKey(prefix: String, keyExpress: String, args: Array<Any>): String {
        val builder = StringBuilder()
        for (arg in args) {
            builder.append(",")
            if (arg is Int || arg is Long
                    || arg is Float || arg is Double || arg is Boolean
                    || arg is String || arg is Char || arg is Byte) {
                builder.append(arg.toString())
            } else {
                try {
                    builder.append(objectMapper.writeValueAsString(arg))
                } catch (e: JsonProcessingException) {
                    builder.append(arg.toString())
                }

            }
        }
        val keyValue = prefix + ":" + MD5Utils.encrypt(builder.toString())
        return keyExpress.replace(DuplicateSubmission.defaultKey, keyValue)
    }

}
