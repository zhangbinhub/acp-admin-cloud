package pers.acp.admin.config.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pers.acp.admin.config.hystrix.OauthServerHystrix;
import pers.acp.spring.boot.exceptions.ServerException;

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
@Component
@FeignClient(value = "oauth2-server", fallbackFactory = OauthServerHystrix.class)
public interface OauthServer {

    /**
     * 刷新配置信息
     * note: 所有服务
     */
    @RequestMapping(value = "/actuator/bus-refresh", method = RequestMethod.POST)
    void busRefresh() throws ServerException;

    /**
     * 刷新配置信息
     * note: 指定服务名
     *
     * @param application 服务名
     */
    @RequestMapping(value = "/actuator/bus-refresh/{application}:**", method = RequestMethod.POST)
    void busRefresh(@PathVariable(value = "application") String application) throws ServerException;

    /**
     * 刷新配置信息
     * note: 匹配表达式
     * exp: oauth-server:8999:**
     *
     * @param matcher 匹配表达式 app:index:id
     *                - app is the vcap.application.name, if it exists, or spring.application.name
     *                - index is the vcap.application.instance_index, if it exists, spring.application.index, local.server.port, server.port, or 0 (in that order).
     *                - id is the vcap.application.instance_id, if it exists, or a random value.
     */
    @RequestMapping(value = "/actuator/bus-refresh/{matcher}", method = RequestMethod.POST)
    void busRefreshMatcher(@PathVariable(value = "matcher") String matcher) throws ServerException;

}
