package pers.acp.admin.route;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author zhangbin by 28/04/2018 15:49
 * @since JDK 11
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RouteServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {

    @Autowired
    TestRestTemplate testRestTemplate;

}
