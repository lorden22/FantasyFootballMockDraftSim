package com.mockdraft.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:webtest;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.bcrypt-strength=4",
        "app.mail.log-reset-links=true"})
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    private static String uniqueName() {
        return "u" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private Cookie register(String username) throws Exception {
        String ip = "10.1." + (int) (Math.random() * 250) + "." + (int) (Math.random() * 250);
        MvcResult result = mvc.perform(post("/api/auth/register").with(csrf()).with(r -> { r.setRemoteAddr(ip); return r; })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"a decent passphrase\",\"email\":null}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andReturn();
        Cookie session = result.getResponse().getCookie("md_session");
        assertThat(session).isNotNull();
        assertThat(session.isHttpOnly()).isTrue();
        assertThat(session.getSecure()).isTrue();
        assertThat(result.getResponse().getHeader("Set-Cookie")).contains("SameSite=Strict");
        return session;
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder b, String body) {
        return b.with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void protectedEndpointsRequireSession() throws Exception {
        mvc.perform(get("/api/drafts")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message").exists());
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/drafts").cookie(new Cookie("md_session", "forged-token-value-that-is-not-real-xxxxxxxx")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mutatingRequestsRequireCsrfToken() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"x\",\"password\":\"y\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void securityHeadersArePresent() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'self'")))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().exists("Permissions-Policy"));
    }

    @Test
    void fullDraftFlowOverHttp() throws Exception {
        String user = uniqueName();
        Cookie session = register(user);

        mvc.perform(get("/api/auth/me").cookie(session)).andExpect(status().isOk()).andExpect(jsonPath("$.username").value(user));

        MvcResult created = mvc.perform(json(post("/api/drafts"), "{\"teamName\":\"Gridiron Gang\",\"numTeams\":6,\"draftPosition\":3}")
                        .cookie(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.teams", hasSize(6)))
                .andExpect(jsonPath("$.currentPick.slot").value(1))
                .andExpect(jsonPath("$.nextUserPick.picksUntil").value(2))
                .andReturn();
        long id = json.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(json(post("/api/drafts"), "{\"teamName\":\"Second\",\"numTeams\":6,\"draftPosition\":1}").cookie(session))
                .andExpect(status().isConflict());

        mvc.perform(json(post("/api/drafts/" + id + "/picks"), "{\"playerRank\":1}").cookie(session))
                .andExpect(status().isConflict());

        mvc.perform(json(post("/api/drafts/" + id + "/simulate"), "").cookie(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.picks", hasSize(2)))
                .andExpect(jsonPath("$.currentPick.isUser").value(true));

        MvcResult avail = mvc.perform(get("/api/drafts/" + id + "/available").cookie(session)).andExpect(status().isOk()).andReturn();
        JsonNode players = json.readTree(avail.getResponse().getContentAsString());
        assertThat(players.size()).isEqualTo(538);
        int rank = players.get(0).get("rank").asInt();

        mvc.perform(json(post("/api/drafts/" + id + "/picks"), "{\"playerRank\":" + rank + "}").cookie(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.picks[2].player.rank").value(rank))
                .andExpect(jsonPath("$.picks[2].slot").value(3));

        JsonNode state = null;
        for (int i = 0; i < 40; i++) {
            state = json.readTree(mvc.perform(json(post("/api/drafts/" + id + "/simulate"), "").cookie(session))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            if ("COMPLETE".equals(state.get("status").asText())) {
                break;
            }
            state = json.readTree(mvc.perform(json(post("/api/drafts/" + id + "/auto-pick"), "").cookie(session))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            if ("COMPLETE".equals(state.get("status").asText())) {
                break;
            }
        }
        assertThat(state.get("status").asText()).isEqualTo("COMPLETE");
        assertThat(state.get("picks").size()).isEqualTo(90);
        assertThat(state.get("currentPick").isNull()).isTrue();

        mvc.perform(get("/api/drafts/" + id + "/teams/3/roster").cookie(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isUser").value(true))
                .andExpect(jsonPath("$.starters", hasSize(9)))
                .andExpect(jsonPath("$.bench", hasSize(6)));

        mvc.perform(get("/api/drafts").cookie(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETE"))
                .andExpect(jsonPath("$[0].picksMade").value(90));

        // another user cannot see or touch this draft
        Cookie other = register(uniqueName());
        mvc.perform(get("/api/drafts/" + id).cookie(other)).andExpect(status().isNotFound());
        mvc.perform(get("/api/drafts/" + id + "/teams/1/roster").cookie(other)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/drafts/" + id).with(csrf()).cookie(other)).andExpect(status().isNotFound());

        mvc.perform(delete("/api/drafts/" + id).with(csrf()).cookie(session)).andExpect(status().isNoContent());
        mvc.perform(get("/api/drafts/" + id).cookie(session)).andExpect(status().isNotFound());
    }

    @Test
    void validationErrorsHaveFieldDetails() throws Exception {
        mvc.perform(json(post("/api/auth/register"), "{\"username\":\"no spaces allowed\",\"password\":\"whatever123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.username").exists());
        mvc.perform(json(post("/api/auth/register"), "not json")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request."));
    }

    @Test
    void logoutRevokesSessionServerSide() throws Exception {
        Cookie session = register(uniqueName());
        mvc.perform(post("/api/auth/logout").with(csrf()).cookie(session)).andExpect(status().isNoContent());
        mvc.perform(get("/api/auth/me").cookie(session)).andExpect(status().isUnauthorized());
    }

    @Test
    void accountChangesNeedCurrentPassword() throws Exception {
        Cookie session = register(uniqueName());
        mvc.perform(json(put("/api/account/email"), "{\"email\":\"me@example.com\",\"currentPassword\":\"wrong\"}").cookie(session))
                .andExpect(status().isBadRequest());
        mvc.perform(json(put("/api/account/email"), "{\"email\":\"Me@Example.com\",\"currentPassword\":\"a decent passphrase\"}")
                        .cookie(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("me@example.com"));
        mvc.perform(json(post("/api/account/delete"), "{\"password\":\"a decent passphrase\"}").cookie(session))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/auth/me").cookie(session)).andExpect(status().isUnauthorized());
    }

    @Test
    void passwordResetDoesNotRevealAccounts() throws Exception {
        mvc.perform(json(post("/api/auth/password-reset"), "{\"email\":\"nobody@example.com\"}"))
                .andExpect(status().isAccepted());
        mvc.perform(json(post("/api/auth/password-reset/validate"), "{\"token\":\"bogus\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void loginIsRateLimited() throws Exception {
        String user = uniqueName();
        register(user);
        int last = 0;
        for (int i = 0; i < 6; i++) {
            last = mvc.perform(json(post("/api/auth/login"), "{\"username\":\"" + user + "\",\"password\":\"wrong pass\"}")
                    .with(r -> { r.setRemoteAddr("10.9.9.9"); return r; }))
                    .andReturn().getResponse().getStatus();
        }
        assertThat(last).isEqualTo(429);
    }
}
