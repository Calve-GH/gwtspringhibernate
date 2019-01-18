package com.github.calve.test.part.observer;

import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan(basePackages = {
    "com.github.calve"
})
public class SpringConfig extends WebMvcConfigurationSupport {
  @Override
  protected void configureViewResolvers(ViewResolverRegistry registry) {
    registry.jsp("/WEB-INF/jsp/", ".jsp");
  }

  @Bean
  protected CacheManager concurrentMapCacheManager() {
    return new ConcurrentMapCacheManager();
  }

  @Bean
  protected ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
