package pers.acp.admin.log.feign.hystrix;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.admin.common.base.BaseFeignHystrix;
import pers.acp.admin.common.enumeration.RuntimeInfoEnum;
import pers.acp.admin.common.vo.RuntimeConfigVO;
import pers.acp.admin.log.feign.Oauth;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Component
public class OauthHystrix extends BaseFeignHystrix<Oauth> {

    @Autowired
    protected OauthHystrix(LogInstance logInstance, ObjectMapper objectMapper) {
        super(logInstance, objectMapper);
    }

    @Override
    public Oauth create(Throwable cause) {
        logInstance.error("调用 oauth-server 异常: " + cause.getMessage(), cause);
        return name -> {
            RuntimeConfigVO runtimeConfigVO = new RuntimeConfigVO();
            try {
                RuntimeInfoEnum runtimeInfoEnum = RuntimeInfoEnum.getEnum(name);
                runtimeConfigVO.setName(runtimeInfoEnum.getName());
                runtimeConfigVO.setValue(runtimeInfoEnum.getValue());
                runtimeConfigVO.setConfigDes(runtimeInfoEnum.getConfigDes());
                runtimeConfigVO.setEnabled(runtimeInfoEnum.isEnabled());
                logInstance.info("返回默认信息：" + objectMapper.writeValueAsString(runtimeConfigVO));
                return runtimeConfigVO;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

}
