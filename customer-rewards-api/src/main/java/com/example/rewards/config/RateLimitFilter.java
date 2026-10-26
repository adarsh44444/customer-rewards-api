package com.example.rewards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.rewards.dto.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final int capacity;
    private final long windowMillis;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public RateLimitFilter(
            ObjectMapper objectMapper,
            @Value("${rewards.rate-limit.capacity:60}") int capacity,
            @Value("${rewards.rate-limit.window-seconds:60}") long windowSeconds) {
        this.objectMapper = objectMapper;
        this.capacity = capacity;
        this.windowMillis = windowSeconds * 1_000L;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String key = request.getRemoteAddr();
        long now = System.currentTimeMillis();
        WindowCounter counter = counters.compute(key, (ignored, current) -> {
            if (current == null || now - current.windowStart >= windowMillis) {
                return new WindowCounter(now, new AtomicInteger(1));
            }
            current.count.incrementAndGet();
            return current;
        });

        if (counter.count.get() > capacity) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After",
                    String.valueOf(Math.max(1, (counter.windowStart + windowMillis - now) / 1_000L)));
            objectMapper.writeValue(response.getOutputStream(), new ApiError(
                    Instant.now(), 429, "Too Many Requests",
                    "Rate limit exceeded. Please retry after the current time window.",
                    request.getRequestURI(), Map.of()));
            return;
        }
        chain.doFilter(request, response);
    }

    private record WindowCounter(long windowStart, AtomicInteger count) {}
}
