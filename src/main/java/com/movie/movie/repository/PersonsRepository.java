package com.movie.movie.repository;

import com.movie.movie.entity.Persons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonsRepository extends JpaRepository<Persons, Long> {
    List<Persons> findByFullNameContaining(String keyword);
}
