package pers.acp.springcloud.common.log;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author zhangbin by 11/07/2018 14:34
 * @since JDK 11
 */
public interface LogOutput {

    @Output(LogConstant.OUTPUT)
    MessageChannel sendMessage();

}
