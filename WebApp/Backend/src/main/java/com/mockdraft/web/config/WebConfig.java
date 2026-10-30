package com.mockdraft.web.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Vite emits content-hashed files under /assets, so they can be cached forever. */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable());
    }

    /** Client-side routes of the React app; each serves index.html and React Router takes over. */
    @Controller
    static class SpaRoutes {
        @GetMapping({"/login", "/register", "/forgot-password", "/reset-password", "/dashboard", "/account",
                "/drafts/{id}", "/drafts/{id}/review"})
        public String index() {
            return "forward:/index.html";
        }
    }
}
