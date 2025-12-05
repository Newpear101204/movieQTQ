package com.movie.movie.repository;

import com.movie.movie.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    boolean existsByPhoneOrEmail(String phone , String email);
}
