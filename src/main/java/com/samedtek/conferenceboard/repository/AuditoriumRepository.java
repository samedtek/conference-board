package com.samedtek.conferenceboard.repository;

import com.samedtek.conferenceboard.entitiy.Auditorium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {

    Integer countAllByIdIsNotNull();

}
