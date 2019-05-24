package pers.acp.admin.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pers.acp.admin.common.annotation.DuplicateSubmission;
import pers.acp.admin.common.lock.DistributedLock;
import pers.acp.core.security.MD5Utils;
import pers.acp.springboot.core.exceptions.ServerException;

import java.lang.reflect.Method;

/**
 * controller拦截器
 *
 * @author zhangbin by 21/11/2017 10:06
 * @since JDK 11
 */
@Aspect
@Component
@Order(0)
public class RestControllerRepeatAspect {

    private final DistributedLock distributedLock;

    private final ObjectMapper objectMapper;

    @Autowired
    public RestControllerRepeatAspect(DistributedLock distributedLock, ObjectMapper objectMapper) {
        this.distributedLock = distributedLock;
        this.objectMapper = objectMapper;
    }

    /**
     * 定义拦截规则
     */
    @Pointcut(value = "execution(public * *(..)) && @annotation(pers.acp.admin.common.annotation.DuplicateSubmission)")
    public void executeService() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp 拦截对象
     * @return Object（被拦截方法的执行结果）
     */
    @Around("executeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        DuplicateSubmission duplicateSubmission = method.getAnnotation(DuplicateSubmission.class);
        String key = getKey(signature.getDeclaringTypeName() + "." + method.getName(), duplicateSubmission.keyExpress(), pjp.getArgs());
        long expire = duplicateSubmission.expire();
        try {
            Object response;
            if (distributedLock.getLock(key, key, expire)) {
                response = pjp.proceed();
                distributedLock.releaseLock(key, key);
            } else {
                throw new ServerException("请勿重复请求");
            }
            return response;
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    private String getKey(String prefix, String keyExpress, Object[] args) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(",");
            if (arg instanceof Integer || arg instanceof Long
                    || arg instanceof Float || arg instanceof Double || arg instanceof Boolean
                    || arg instanceof String || arg instanceof Character || arg instanceof Byte) {
                builder.append(arg.toString());
            } else {
                try {
                    builder.append(objectMapper.writeValueAsString(arg));
                } catch (JsonProcessingException e) {
                    builder.append(arg.toString());
                }
            }
        }
        String keyValue = prefix + ":" + MD5Utils.encrypt(builder.toString());
        return keyExpress.replace(DuplicateSubmission.KEY, keyValue);
    }

}
