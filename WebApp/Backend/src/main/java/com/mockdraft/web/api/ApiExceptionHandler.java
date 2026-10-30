package com.mockdraft.web.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.mockdraft.core.service.AuthenticationException;
import com.mockdraft.core.service.ConflictException;
import com.mockdraft.core.service.NotFoundException;
import com.mockdraft.core.service.RateLimitedException;
import com.mockdraft.core.service.ValidationException;

/** Maps failures to the documented error body. Internal details are logged, never returned. */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> validation(ValidationException e) {
        Map<String, Object> body = body(HttpStatus.BAD_REQUEST, e.getMessage());
        body.put("fields", e.fields());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> authentication(AuthenticationException e) {
        return respond(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(NotFoundException e) {
        return respond(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Unknown API paths and missing files get a JSON 404. Other unknown page URLs get the SPA shell with a 404
     * status so the React app can render its own "page not found" screen.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> noResource(NoResourceFoundException e, jakarta.servlet.http.HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean pageRequest = !path.startsWith("/api/") && !path.substring(path.lastIndexOf('/') + 1).contains(".");
        if (pageRequest && SPA_SHELL.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(org.springframework.http.MediaType.TEXT_HTML)
                    .cacheControl(org.springframework.http.CacheControl.noCache()).body(SPA_SHELL);
        }
        return respond(HttpStatus.NOT_FOUND, "Not found.");
    }

    private static final org.springframework.core.io.Resource SPA_SHELL =
            new org.springframework.core.io.ClassPathResource("static/index.html");

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> conflict(ConflictException e) {
        return respond(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(RateLimitedException.class)
    public ResponseEntity<Map<String, Object>> rateLimited(RateLimitedException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).header("Retry-After", "60")
                .body(body(HttpStatus.TOO_MANY_REQUESTS, e.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> malformed(Exception e) {
        return respond(HttpStatus.BAD_REQUEST, "Malformed request.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> method(HttpRequestMethodNotSupportedException e) {
        return respond(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> mediaType(HttpMediaTypeNotSupportedException e) {
        return respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Send JSON with Content-Type: application/json.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unexpected(Exception e) {
        log.error("Unhandled error", e);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.");
    }

    private static ResponseEntity<Map<String, Object>> respond(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(body(status, message));
    }

    private static Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
