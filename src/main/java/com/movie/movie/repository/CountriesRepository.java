package com.movie.movie.repository;

import com.movie.movie.entity.Countries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountriesRepository extends JpaRepository<Countries, Integer> {
    Countries findByCountryName(String country);
}
