package hu.webuni.gateway.filter;

import hu.thesis.security.JwtAuthFilter;
import hu.thesis.security.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Component
public class GatewayFilter implements GlobalFilter {

    @Autowired
    private JwtTokenService jwtTokenService;
    private String loginPathPattern = "/user/";
    private String orderPathPattern = "/order/";
    private String catalogPathPattern = "/catalog/";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpMethod method = exchange.getRequest().getMethod();
        String requestPath = getRequestPath(exchange);
        if(requestPath.startsWith(loginPathPattern))
            return chain.filter(exchange);

        if(requestPath.startsWith(orderPathPattern)) {
            checkAuthorization(exchange);
            return chain.filter(exchange);
        }
        if(requestPath.startsWith(catalogPathPattern)) {
            if (method != HttpMethod.GET)
                checkAuthorization(exchange);
            return chain.filter(exchange);
        }
            checkAuthorization(exchange);
            return chain.filter(exchange);
    }

    private void checkAuthorization(ServerWebExchange exchange) {
        List<String> authHeaders = exchange.getRequest().getHeaders().get("Authorization");
        if(ObjectUtils.isEmpty(authHeaders)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } else {
            String authHeader = authHeaders.get(0);
            UsernamePasswordAuthenticationToken userDetails = null;
            try {
                userDetails = JwtAuthFilter.createUserDetailsFromAuthHeader(authHeader, jwtTokenService);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (userDetails == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    private String getRequestPath(ServerWebExchange exchange) {
        Set<URI> origUrls = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        URI originalUri = origUrls.iterator().next();
        String subPath = PathContainer.parsePath(originalUri.toString()).subPath(4).value();
        return subPath;
    }
}
