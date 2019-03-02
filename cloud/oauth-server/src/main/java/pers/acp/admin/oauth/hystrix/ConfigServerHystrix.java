package pers.acp.admin.oauth.hystrix;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.admin.common.base.BaseFeignHystrix;
import pers.acp.admin.oauth.feign.ConfigServer;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
@Component
public class ConfigServerHystrix extends BaseFeignHystrix<ConfigServer> {

    @Autowired
    protected ConfigServerHystrix(LogInstance logInstance, ObjectMapper objectMapper) {
        super(logInstance, objectMapper);
    }

    @Override
    public ConfigServer create(Throwable cause) {
        logInstance.error("调用 config-server 异常: " + cause.getMessage(), cause);
        return new ConfigServer() {
            @Override
            public void busRefresh() throws ServerException {
                String errMsg = "配置信息刷新失败";
                logInstance.info(errMsg);
                throw new ServerException(errMsg);
            }

            @Override
            public void busRefresh(String application) throws ServerException {
                String errMsg = "配置信息刷新失败: application.name = " + application;
                logInstance.info(errMsg);
                throw new ServerException(errMsg);
            }

            @Override
            public void busRefreshMatcher(String matcher) throws ServerException {
                String errMsg = "配置信息刷新失败: matcher = " + matcher;
                logInstance.info(errMsg);
                throw new ServerException(errMsg);
            }
        };
    }

}
