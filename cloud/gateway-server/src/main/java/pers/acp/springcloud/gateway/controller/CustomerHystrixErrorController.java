package pers.acp.springcloud.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.springcloud.gateway.vo.ErrorVO;
import reactor.core.publisher.Mono;

/**
 * @author zhangbin by 26/04/2018 21:30
 * @since JDK 11
 */
@RestController
public class CustomerHystrixErrorController {

    /**
     * 服务断路 Hystrix 响应
     *
     * @return ResponseEntity
     */
    @RequestMapping(value = "/hystrixhandle", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<ResponseEntity<Object>> hystrixHandle() {
        ErrorVO errorVO = new ErrorVO();
        errorVO.setError("invalid service");
        errorVO.setErrorDescription("GateWay error, the service is invalid");
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorVO));
    }

}
