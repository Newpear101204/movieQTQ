package com.movie.movie.service.impl;

import com.movie.movie.entity.Persons;
import com.movie.movie.entity.UserActorList;
import com.movie.movie.entity.Users;
import com.movie.movie.repository.PersonsRepository;
import com.movie.movie.repository.UserActorListRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.service.UserActorListService;
import jakarta.transaction.Transactional;
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

    @Override
    @Transactional // Quan trọng khi thực hiện thao tác xóa
    public void removeActorFromList(Long personId) {
        // 1. Lấy user hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = UsersRepository.findByUsername(username);

        // 2. Lấy diễn viên
        Persons person = personsRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Diễn viên không tồn tại với ID: " + personId));

        // 3. Tìm bản ghi trong bảng trung gian
        UserActorList actorListItem = userActorListRepository.findByUserAndPerson(user, person);

        // 4. Xóa nếu tìm thấy
        if (actorListItem != null) {
            userActorListRepository.delete(actorListItem);
        } else {
            throw new RuntimeException("Diễn viên này chưa có trong danh sách yêu thích của bạn");
        }
    }
}
