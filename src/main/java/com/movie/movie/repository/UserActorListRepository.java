package com.movie.movie.repository;

import com.movie.movie.entity.Persons;
import com.movie.movie.entity.UserActorList;
import com.movie.movie.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActorListRepository extends JpaRepository<UserActorList, Long> {
    UserActorList findByUserAndPerson(Users user, Persons person);
    boolean existsByUserAndPerson(Users user, Persons person);
}
