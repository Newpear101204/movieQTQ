package com.movie.movie.service.impl;

import com.movie.movie.entity.Persons;
import com.movie.movie.entity.UserActorList;
import com.movie.movie.entity.Users;
import com.movie.movie.repository.PersonsRepository;
import com.movie.movie.repository.UserActorListRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.service.UserActorListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserActorListServiceImpl implements UserActorListService {

    @Autowired
    private UserActorListRepository userActorListRepository;

    @Autowired
    private UsersRepository UsersRepository;

    @Autowired
    private PersonsRepository personsRepository;

    @Override
    public void addToActorList(Long id) {
        Persons persons = personsRepository.findById(id).get();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = UsersRepository.findByUsername(username);
        UserActorList userActorList = new UserActorList();
        userActorList.setUser(users);
        userActorList.setPerson(persons);
        userActorListRepository.save(userActorList);
    }
}
