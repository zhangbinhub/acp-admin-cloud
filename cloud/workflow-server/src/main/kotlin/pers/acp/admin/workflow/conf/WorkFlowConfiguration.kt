package pers.acp.admin.workflow.conf

import org.flowable.spring.SpringProcessEngineConfiguration
import org.flowable.spring.boot.EngineConfigurationConfigurer
import org.springframework.context.annotation.Configuration

/**
 * @author zhang by 12/06/2019
 * @since JDK 11
 */
@Configuration
class WorkFlowConfiguration : EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    override fun configure(engineConfiguration: SpringProcessEngineConfiguration) {
        engineConfiguration.activityFontName = "宋体"
        engineConfiguration.labelFontName = "宋体"
        engineConfiguration.annotationFontName = "宋体"
    }

}
