package pers.acp.admin.common.conf

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author zhang by 30/09/2019
 * @since JDK 11
 */
@ConfigurationProperties(prefix = "acp.cloud.zookeeper")
class ZkClientConfiguration {

    var connect: String = ""

    var sessionTimeOut: Int = 60 * 1000

    var connectionTimeOut: Int = 15 * 1000

}