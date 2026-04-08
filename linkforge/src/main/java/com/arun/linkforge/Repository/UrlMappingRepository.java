package com.arun.linkforge.Repository;

import com.arun.linkforge.Models.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    public Optional<UrlMapping> findByLongUrl(String longurl);
    Optional<UrlMapping> findByShortCode(String shortCode);
}
