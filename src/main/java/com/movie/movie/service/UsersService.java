package com.movie.movie.service;

import com.movie.movie.model.dto.ChangePasswordDTO;
import com.movie.movie.model.dto.LoginDTO;
import com.movie.movie.model.dto.RegisterDTO;
import com.movie.movie.model.dto.UpdateUserDTO;
import com.movie.movie.model.response.CastResponse;
import com.movie.movie.model.response.LoginResponse;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.model.response.UsersResponse;

import java.util.List;

public interface UsersService {
    LoginResponse login (LoginDTO loginDTO);
    void register (RegisterDTO registerDTO);
    List<UsersResponse> allUsers ();
    void deleteUser (Long id);
    void changePassword(ChangePasswordDTO request);
    void updateUser ( UpdateUserDTO updateUserDTO);
    void addHistory (Long id);     // pending
    List<MovieResponse> movieOfWatchList();
    List<CastResponse> castOfWatchList();
    UsersResponse getMyInfo();
}
