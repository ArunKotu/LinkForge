package com.arun.linkforge.Services;

import com.arun.linkforge.DTO.ResourceNotFoundException;
import com.arun.linkforge.Models.UrlMapping;
import com.arun.linkforge.Repository.UrlMappingRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class UrlMappingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UrlMappingRepository repository;
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    public UrlMappingService(RedisTemplate<String, String> redisTemplate,
                             UrlMappingRepository repository) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
    }

    public String shorten(UrlMapping url, HttpServletRequest request) {
        String cached = redisTemplate.opsForValue().get(url.getLongUrl());
        if (cached != null) {
            return baseUrl(request) + "/" + cached;
        }

        Optional<UrlMapping> existing = repository.findByLongUrl(url.getLongUrl());
        if (existing.isPresent()) {
            UrlMapping existingUrl = existing.get();
            redisTemplate.opsForValue().set(url.getLongUrl(), existingUrl.getShortCode(), Duration.ofDays(1));
            redisTemplate.opsForValue().set(existingUrl.getShortCode(), url.getLongUrl(), Duration.ofDays(1));
            return baseUrl(request) + "/" + existingUrl.getShortCode();
        }

        UrlMapping saved = repository.save(url);
        Long id = saved.getId();
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(BASE62.charAt((int) (id % 62)));
            id /= 62;
        }
        String shorten = paddingLeft(sb.reverse().toString());
        saved.setShortCode(shorten);

        redisTemplate.opsForValue().set(url.getLongUrl(), shorten, Duration.ofDays(1));
        redisTemplate.opsForValue().set(shorten, url.getLongUrl(), Duration.ofDays(1));

        repository.save(saved);
        return baseUrl(request) + "/" + shorten;
    }

    public String redirect(String shortCode) {
        System.out.println("Incoming shortCode: " + shortCode);

        String cached = redisTemplate.opsForValue().get(shortCode);
        System.out.println("Redis value: " + cached);

        if (cached != null) {
            return cached.trim();
        }

        Optional<UrlMapping> existing = repository.findByShortCode(shortCode);
        if (existing.isPresent()) {
            String longUrl = existing.get().getLongUrl();
            System.out.println("DB value: " + longUrl);
            redisTemplate.opsForValue().set(shortCode,existing.get().getLongUrl());
            redisTemplate.opsForValue().set(existing.get().getLongUrl(),shortCode);
            return longUrl;
        }

        System.out.println("NOT FOUND");
        throw new ResourceNotFoundException("Resource not Found!");
    }

    private String paddingLeft(String input) {
        return ("0000000" + input).substring(input.length());
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getRequestURL().toString()
                .replace(request.getRequestURI(), "");
    }
}
