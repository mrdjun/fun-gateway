package com.fun.gateway.config;

import com.alibaba.fastjson.JSON;
import com.fun.gateway.constant.GatewayConstants;
import com.fun.gateway.domain.R;
import com.fun.gateway.utils.JwtUtils;
import com.fun.gateway.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * 网关鉴权过滤器
 *  此网关的 JwtUtils 搭配着 fun-security 中的 auth 使用，其主要作为统一授权中心使用，
 *  由 auth 颁发令牌，网关只做鉴权不做授权
 *
 * @author MrDJun 2020/10/26
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    // 公钥的存放路径，不对外暴露
    @Value("${rsa.public-key}")
    private String publicKeyPath;
    // 是否开启网关鉴权功能，测试sentinel 限流时关闭
    @Value("${security.enable}")
    private boolean enable;

    protected PublicKey publicKey;

    @PostConstruct
    public void createRsaKey() {
        try {
            publicKey = RsaUtils.getPublicKey(publicKeyPath);
        } catch (Exception e) {
            log.error("[网关内部错误]公钥不正确或不存在");
        }
    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = getToken(exchange.getRequest());
        // 是否开启验证
        if (!enable) {
            return chain.filter(exchange);
        }
        if (token == null) {
            log.error("[网关鉴权异常]令牌格式不正确 {} -> {}", exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath());
            return setUnauthorizedResponse(exchange);
        }

        try {
            JwtUtils.parserToken(token, publicKey);
        } catch (Exception e) {
            log.info("[网关鉴权异常]令牌过期或无效");
            return setUnauthorizedResponse(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            return bufferFactory.wrap(JSON.toJSONBytes(R.unauth()));
        }));
    }

    // 获取真正的 jwt
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(GatewayConstants.HEADER_NAME);
        if (token != null && token.startsWith(GatewayConstants.TOKEN_PREFIX)) {
            token = token.replace(GatewayConstants.TOKEN_PREFIX, "");
        }
        return token;
    }

    // Ordered 负责filter的顺序，越小越优先
    public int getOrder() {
        return -200;
    }
}
