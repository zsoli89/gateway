package hu.webuni.gateway;

import hu.webuni.security.JwtAuthFilter;
import hu.webuni.security.JwtTokenService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackageClasses = {GatewayApplication.class, JwtAuthFilter.class, JwtTokenService.class})
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
