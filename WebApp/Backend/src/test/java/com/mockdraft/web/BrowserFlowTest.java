package com.mockdraft.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/** Exercises the real cookie + CSRF handshake exactly as the browser SPA performs it, over HTTP. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:browsertest;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.bcrypt-strength=4",
        // the JDK client won't return Secure cookies over plain http
        "app.secure-cookies=false"})
class BrowserFlowTest {

    @LocalServerPort
    int port;

    @Test
    void csrfHandshakeAndCookieSession() throws Exception {
        CookieManager jar = new CookieManager();
        HttpClient client = HttpClient.newBuilder().cookieHandler(jar).build();
        String base = "http://localhost:" + port;

        HttpResponse<String> csrf = client.send(HttpRequest.newBuilder(URI.create(base + "/api/auth/csrf")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(csrf.statusCode()).isEqualTo(204);
        String token = jar.getCookieStore().getCookies().stream().filter(c -> c.getName().equals("XSRF-TOKEN"))
                .map(HttpCookie::getValue).findFirst().orElseThrow();

        String body = "{\"username\":\"browser_user\",\"password\":\"a decent passphrase\"}";
        HttpResponse<String> noHeader = client.send(post(base + "/api/auth/register", body, null), HttpResponse.BodyHandlers.ofString());
        assertThat(noHeader.statusCode()).isEqualTo(403);

        HttpResponse<String> wrongHeader = client.send(post(base + "/api/auth/register", body, "not-the-token"),
                HttpResponse.BodyHandlers.ofString());
        assertThat(wrongHeader.statusCode()).isEqualTo(403);

        HttpResponse<String> registered = client.send(post(base + "/api/auth/register", body, token), HttpResponse.BodyHandlers.ofString());
        assertThat(registered.statusCode()).isEqualTo(201);
        assertThat(registered.headers().allValues("Set-Cookie")).anyMatch(c -> c.startsWith("md_session=") && c.contains("HttpOnly"));

        HttpResponse<String> me = client.send(HttpRequest.newBuilder(URI.create(base + "/api/auth/me")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(me.statusCode()).isEqualTo(200);
        assertThat(me.body()).contains("browser_user");

        // unknown API paths are JSON 404s, never the SPA shell
        HttpResponse<String> unknown = client.send(HttpRequest.newBuilder(URI.create(base + "/api/nope")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(unknown.statusCode()).isEqualTo(404);
    }

    private static HttpRequest post(String url, String json, String csrfToken) {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (csrfToken != null) {
            b.header("X-XSRF-TOKEN", csrfToken);
        }
        return b.build();
    }
}
