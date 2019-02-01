package pers.acp.admin.log.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.acp.admin.common.constant.path.CommonPath;
import pers.acp.admin.common.constant.path.oauth.OauthOpenInnerApi;
import pers.acp.admin.common.vo.RuntimeConfigVO;
import pers.acp.admin.log.feign.hystrix.OauthHystrix;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Component
@FeignClient(value = "oauth2-server", fallbackFactory = OauthHystrix.class)
public interface Oauth {

    @GetMapping(value = CommonPath.openInnerBasePath + OauthOpenInnerApi.runtimeConfig + "/{name}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    RuntimeConfigVO findRuntimeByName(@PathVariable(value = "name") String name);

}
