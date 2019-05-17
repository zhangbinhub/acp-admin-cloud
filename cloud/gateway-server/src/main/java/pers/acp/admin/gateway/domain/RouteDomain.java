package pers.acp.admin.gateway.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
public class RouteDomain {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ThreadPoolExecutor executor;

    public RouteDomain() {
        executor = new ThreadPoolExecutor(0, 100, 60000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    public void beforeRoute(ServerHttpRequest serverHttpRequest) {
        executor.execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("请求开始...");
            logger.info(serverHttpRequest.getPath().value());
        });
    }

    public void afterRoute(ServerHttpResponse serverHttpResponse) {
        executor.execute(() -> {
            if (serverHttpResponse != null) {
                logger.info("status=" + serverHttpResponse.getStatusCode().value());
                logger.info("请求结束");
            }
        });
    }

}
