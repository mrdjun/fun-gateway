package com.fun.gateway.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.fun.gateway.domain.R;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import reactor.core.publisher.Mono;

/**
 * 黑名单过滤器
 *
 * @author MrDJun
 */
@Component
public class BlackListUrlFilter extends AbstractGatewayFilterFactory<BlackListUrlFilter.Config> {
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String url = exchange.getRequest().getURI().getPath();
            if (config.matchBlacklist(url)) {
                ServerHttpResponse response = exchange.getResponse();
                return exchange.getResponse().writeWith(
                        Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(R.fail("服务拒绝访问")))));
            }

            return chain.filter(exchange);
        };
    }

    public BlackListUrlFilter() {
        super(Config.class);
    }

    public static class Config {
        private List<String> blacklistUrl;

        private final List<Pattern> blacklistUrlPattern = new ArrayList<>();

        public boolean matchBlacklist(String url) {
            return !blacklistUrlPattern.isEmpty() && blacklistUrlPattern.stream().anyMatch(p -> p.matcher(url).find());
        }

        public List<String> getBlacklistUrl() {
            return blacklistUrl;
        }

        public void setBlacklistUrl(List<String> blacklistUrl) {
            this.blacklistUrl = blacklistUrl;
            this.blacklistUrlPattern.clear();
            this.blacklistUrl.forEach(url -> {
                this.blacklistUrlPattern.add(Pattern.compile(url.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE));
            });
        }
    }
}
