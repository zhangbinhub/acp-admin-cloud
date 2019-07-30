package pers.acp.admin.oauth.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import pers.acp.admin.oauth.BaseTest;
import pers.acp.admin.oauth.bus.event.RefreshApplicationEvent;

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
class TestJson extends BaseTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BusProperties busProperties;

    @Test
    void testJsonEvent() throws JsonProcessingException {
        RefreshApplicationEvent event = new RefreshApplicationEvent(busProperties.getId(), null, "refresh client", this);
        System.out.println(objectMapper.writeValueAsString(event));
    }

}
