package com.mockdraft.web.config;

import java.time.Clock;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.mockdraft.core.MockDraftCore;
import com.mockdraft.core.service.AuthService;
import com.mockdraft.core.service.DraftService;

/** Exposes the shared core (the same code the console app runs) as Spring beans. */
@Configuration
public class CoreConfig {

    @Bean
    public MockDraftCore mockDraftCore(DataSource dataSource, AppProperties props) {
        return new MockDraftCore(dataSource, Clock.systemUTC(), props.bcryptStrength(), props.sessionTtl(), props.resetTtl());
    }

    @Bean
    public AuthService authService(MockDraftCore core) {
        return core.auth();
    }

    @Bean
    public DraftService draftService(MockDraftCore core) {
        return core.drafts();
    }

    @Bean
    public TokenCleanup tokenCleanup(AuthService auth) {
        return new TokenCleanup(auth);
    }

    /** Periodically removes expired sessions and reset tokens. */
    public static class TokenCleanup {
        private final AuthService auth;

        TokenCleanup(AuthService auth) {
            this.auth = auth;
        }

        @Scheduled(fixedDelayString = "PT1H", initialDelayString = "PT1M")
        public void purge() {
            auth.purgeExpired();
        }
    }
}
