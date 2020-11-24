package com.fun.gateway.config;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 限流回调处理
 * @author MrDJun 2020/11/3
 */
@Component
public class SentinelFallbackHandler implements BlockRequestHandler {

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.valueOf("application/json;charset=UTF-8"))
                .bodyValue("{\"status\":429,\"message\":\"请求过于频繁，请稍后再试\"}");
    }
}
