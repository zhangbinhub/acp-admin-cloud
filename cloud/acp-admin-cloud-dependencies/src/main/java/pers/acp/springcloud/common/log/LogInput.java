package pers.acp.springcloud.common.log;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

/**
 * @author zhangbin by 11/07/2018 14:34
 * @since JDK 11
 */
@Component
public interface LogInput {

    @Input(LogConstant.INPUT)
    SubscribableChannel input();

}
