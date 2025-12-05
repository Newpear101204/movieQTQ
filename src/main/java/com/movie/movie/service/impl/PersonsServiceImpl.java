package com.movie.movie.service.impl;

import com.movie.movie.entity.Persons;
import com.movie.movie.model.response.CastResponse;
import com.movie.movie.repository.PersonsRepository;
import com.movie.movie.service.PersonsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonsServiceImpl implements PersonsService {

    @Autowired
    private PersonsRepository personsRepository;

    @Override
    public List<CastResponse> getCast(String text) {
        List<Persons> persons = personsRepository.findByFullNameContaining(text);
        List<CastResponse> castResponses = new ArrayList<>();
        for (Persons person : persons) {
            CastResponse castResponse = new CastResponse();
            castResponse.setId(person.getId());
            castResponse.setDob(person.getDateOfBirth());
            castResponse.setSlug(person.getSlug());
            castResponse.setName(person.getFullName());
            castResponse.setImage(person.getAvatarUrl());
            castResponse.setGender(person.getGender());
            castResponses.add(castResponse);
        }
        return castResponses;
    }

    @Override
    public void deleteCast(Long id) {
        personsRepository.deleteById(id);
    }
}
