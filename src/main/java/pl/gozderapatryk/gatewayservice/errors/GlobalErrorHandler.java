package pl.gozderapatryk.gatewayservice.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import pl.gozderapatryk.gatewayservice.dto.response.ErrorResponseDto;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Configuration
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

        DataBufferFactory dataBufferFactory = serverWebExchange.getResponse().bufferFactory();
        DataBuffer dataBuffer;
        if (throwable instanceof IllegalArgumentException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            try {
                dataBuffer = dataBufferFactory.wrap(objectMapper.writeValueAsBytes(ErrorResponseDto.builder()
                        .message(throwable.getMessage())
                        .createdAt(LocalDateTime.now())
                        .build()));
            } catch (JsonProcessingException e) {
                dataBuffer = dataBufferFactory.wrap("".getBytes());
            }
            serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
        }
        serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            dataBuffer = dataBufferFactory.wrap(objectMapper.writeValueAsBytes(ErrorResponseDto.builder()
                    .message(throwable.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build()));
        } catch (JsonProcessingException e) {
            dataBuffer = dataBufferFactory.wrap("".getBytes());
        }
        return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}
