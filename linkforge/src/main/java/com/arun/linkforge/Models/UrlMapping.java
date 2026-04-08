package com.arun.linkforge.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "url_mapping")
@Entity
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String longUrl;
    private String shortCode;
    private LocalDateTime createdAt = LocalDateTime.now();
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
