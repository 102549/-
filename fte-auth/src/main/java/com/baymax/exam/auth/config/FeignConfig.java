package com.baymax.exam.auth.config;

import com.baymax.exam.auth.interceptor.FeignInterceptor;
import okhttp3.ConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author ：Baymax
 * @date ：Created in 2022/12/1 9:37
 * @description：配置openfeign
 * @modified By：
 * @version:
 */
@Configuration
public class FeignConfig {
    @Bean
    public FeignInterceptor customFeignInterceptor(){
        return new FeignInterceptor();
    }
    @Bean
    public okhttp3.OkHttpClient okHttpClient(){
        return new okhttp3.OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool())
                .build();
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory(okHttpClient()));
    }
}
