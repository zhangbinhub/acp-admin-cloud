package pers.acp.admin.common.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.hystrix.FallbackFactory;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
public abstract class BaseFeignHystrix<T> implements FallbackFactory<T> {

    protected final LogInstance logInstance;

    protected final ObjectMapper objectMapper;

    protected BaseFeignHystrix(LogInstance logInstance, ObjectMapper objectMapper) {
        this.logInstance = logInstance;
        this.objectMapper = objectMapper;
    }

}
