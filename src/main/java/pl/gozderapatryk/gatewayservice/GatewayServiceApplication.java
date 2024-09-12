package pl.gozderapatryk.gatewayservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import pl.gozderapatryk.gatewayservice.utils.Misc;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class GatewayServiceApplication {

    @Value("${services.userService.baseUrl}")
    private String userServiceBaseUrl;

    public static void main(String[] args) {
        var env = new SpringApplication(GatewayServiceApplication.class).run(args).getEnvironment();
        log.info(Misc.getHelloMessage(env));
    }

    @LoadBalanced
    @Bean(name = "userServiceWebClient")
    public WebClient.Builder userServiceWebClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(userServiceBaseUrl);
    }
}