package pers.acp.admin.workflow.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "workflow-server")
@RefreshScope
class WorkFlowCustomerConfiguration {
    /**
     * 是否开启待办生成通知
     */
    var notifyPendingCreated: Boolean = false

    /**
     * 是否开启待办完成通知
     */
    var notifyPendingFinished: Boolean = false
}