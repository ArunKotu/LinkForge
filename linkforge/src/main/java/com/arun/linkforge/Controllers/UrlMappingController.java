package com.arun.linkforge.Controllers;

import com.arun.linkforge.DTO.RateLimitExceededException;
import com.arun.linkforge.Models.UrlMapping;
import com.arun.linkforge.Services.RateLimitingService;
import com.arun.linkforge.Services.UrlMappingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UrlMappingController {

    private final UrlMappingService urlMappingService;
    private final RateLimitingService rateLimitingService;

    public UrlMappingController(UrlMappingService urlMappingService,
                                RateLimitingService rateLimitingService) {
        this.urlMappingService = urlMappingService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<String> shorten(@RequestBody UrlMapping url, HttpServletRequest request) {
        if (!rateLimitingService.isAllowed(getClientIp(request))) {
            throw new RateLimitExceededException("Too Many Requests");
        }
        String shortUrl = urlMappingService.shorten(url, request);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shorten}")
    public void redirect(@PathVariable String shorten,
                         HttpServletResponse response,
                         HttpServletRequest request) throws IOException {
        if (!rateLimitingService.isAllowed(getClientIp(request))) {
            throw new RateLimitExceededException("Too Many Requests");
        }

        String longUrl = urlMappingService.redirect(shorten);
        if (longUrl != null) {
            response.sendRedirect(longUrl);
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "URL Not Found");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
