package com.movie.movie.service.impl;

import com.movie.movie.convert.EntityToResponse;
import com.movie.movie.entity.*;
import com.movie.movie.exception.AccountExist;
import com.movie.movie.exception.DataNotFoundException;
import com.movie.movie.exception.DuplicatedUsername;
import com.movie.movie.model.dto.LoginDTO;
import com.movie.movie.model.dto.RegisterDTO;
import com.movie.movie.model.dto.UpdateUserDTO;
import com.movie.movie.model.response.*;
import com.movie.movie.repository.MoviesRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.repository.WatchHistoryRepository;
import com.movie.movie.service.MovieService;
import com.movie.movie.service.UsersService;
import com.movie.movie.utils.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MoviesRepository moviesRepository;

    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Override
    public LoginResponse login(LoginDTO loginDTO) {
        LoginResponse loginResponse = new LoginResponse();
        Users users = usersRepository.findByUsername(loginDTO.getUsername());
        if (users == null) {
            throw new DataNotFoundException("Username Or PassWord exists");
        }
        if(!passwordEncoder.matches(loginDTO.getPassword(), users.getPassword())) {
            throw new BadCredentialsException("Wrong phone number or password");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword(),
                users.getAuthorities()
        );

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication); // << Quan trọng
        } catch (AuthenticationException
                ex) {
            // Log hoặc throw lại lỗi để chắc chắn không bị swallow
            throw new BadCredentialsException("Authentication failed", ex);
        }
        try {
            loginResponse.setRole(users.getRole());
            loginResponse.setUsername(users.getUsername());
            loginResponse.setToken(jwtTokenUtil.generateToken(users));
            return loginResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        Users users = modelMapper.map(registerDTO, Users.class);
        if(usersRepository.findByUsername(users.getUsername()) != null){
            throw new DuplicatedUsername
                    ("Username is already in use");
        }
        if(usersRepository.existsByPhoneOrEmail(registerDTO.getPhone(), registerDTO.getEmail())){
            throw new AccountExist("Phone or Email is already in use");
        }
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        users.setPassword(encodedPassword);
        users.setRole("user");
        users.setIsActive(true);
        usersRepository.save(users);
    }

    @Override
    public List<UsersResponse> allUsers() {
        List<Users> usersList = usersRepository.findAll();
        List<UsersResponse> usersResponseList = new ArrayList<>();
        for (Users users : usersList) {
            UsersResponse usersResponse = modelMapper.map(users, UsersResponse.class);
            usersResponseList.add(usersResponse);
        }
        return usersResponseList;
    }

    @Override
    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }

    @Override
    public void updateUser(UpdateUserDTO updateUserDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByUsername(username);
        users.setUsername(updateUserDTO.getUsername());
        users.setPassword(updateUserDTO.getPassword());
        users.setFullName(updateUserDTO.getFullName());
        users.setPhone(updateUserDTO.getPhone());
        users.setDateOfBirth(updateUserDTO.getDateOfBirth());
        users.setGender(updateUserDTO.getGender());
        users.setAvatarUrl(updateUserDTO.getAvatarUrl());
        usersRepository.save(users);
    }

    @Override
    public void addHistory(Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Movies movies = moviesRepository.findById(id).get();


    }

    @Override
    public List<MovieResponse> movieOfWatchList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByUsername(username);
        List<UserWatchList> userWatchLists = users.getWatchLists();
        List<MovieResponse> movieResponseList = new ArrayList<>();
        for (UserWatchList userWatchList : userWatchLists) {
            Movies movies = userWatchList.getMovie();
            MovieResponse moviesResponse = EntityToResponse.convertFromMovie(movies);
            movieResponseList.add(moviesResponse);
        }
        return movieResponseList;
    }

    @Override
    public List<CastResponse> castOfWatchList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByUsername(username);
        List<UserActorList> userActorLists = users.getActorLists();
        List<CastResponse> castResponseList = new ArrayList<>();
        for (UserActorList userActorList : userActorLists) {
            Persons person = userActorList.getPerson();
            CastResponse castResponse = new CastResponse(person.getId(), person.getFullName(),
                    person.getSlug(), person.getAvatarUrl(), person.getGender(),person.getDateOfBirth());
            castResponseList.add(castResponse);
        }
        return castResponseList;
    }


}
