package com.dominiccobo.fyp.stackexchange;

import com.dominiccobo.fyp.stackexchange.provider.StackExchangeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(RestTemplateConfiguration.class);

    @Bean()
    @Scope("prototype")
    RestTemplate restTemplate(@Autowired StackExchangeConfiguration config) {
        LOG.trace("Building instance of rest template from prototype for {}.", config.getApiBaseUrl());
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(config.getApiBaseUrl());

        RestTemplate template = new RestTemplateBuilder()
                .uriTemplateHandler(uriBuilderFactory)
                .build();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingInterceptor());
        template.setInterceptors(interceptors);
        return template;
    }

    public static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            LOG.debug("Sending {} request to {}", request.getMethod(), request.getURI());
            return execution.execute(request, body);
        }
    }
}
