package com.samedtek.conferenceboard.repository;

import com.samedtek.conferenceboard.entitiy.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
}
