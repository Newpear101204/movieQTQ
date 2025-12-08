package com.movie.movie.api;

import com.movie.movie.model.dto.LoginDTO;
import com.movie.movie.model.dto.RegisterDTO;
import com.movie.movie.model.dto.UpdateUserDTO;
import com.movie.movie.model.response.CastResponse;
import com.movie.movie.model.response.LoginResponse;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.model.response.UsersResponse;
import com.movie.movie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/")
public class UserApi {

    @Autowired
    private UsersService usersService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserWatchListService userWatchListService;

    @Autowired
    private WatchHistoryService watchHistoryService;

    @Autowired
    private UserActorListService userActorListService;

    @GetMapping("/login")
    public LoginResponse login (@RequestBody LoginDTO loginDTO) {
        return usersService.login(loginDTO);
    }

    @PostMapping("/register")
    public void register (@RequestBody RegisterDTO registerDTO) {
        usersService.register(registerDTO);
    }

    @PatchMapping("/update")
    public void update(@RequestBody UpdateUserDTO updateUserDTO){
        usersService.updateUser(updateUserDTO);
    }

    @GetMapping("/history")
    public List<MovieResponse> getHistory(){
        return movieService.getHistory();
    }

    @PostMapping("/history/{id}")
    public void addHistory(@PathVariable Long id){
        watchHistoryService.addToHistory(id);
    }

    @PostMapping("/watch-list/{id}")
    public void addToWatchList(@PathVariable Long id){
        userWatchListService.addToWatchList(id);
    }

    @GetMapping("/watch-list")
    public void getWatchList(){
        usersService.movieOfWatchList();
    }

    @GetMapping("/actor")
    public List<CastResponse> getActor(){
       return usersService.castOfWatchList();
    }

    @PostMapping("/actor/{id}")
    public void addActorToWatchList(@PathVariable Long id){
        userActorListService.addToActorList(id);
    }

    @GetMapping
    public List<UsersResponse> getUser(){
        return usersService.allUsers();
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id ){
        usersService.deleteUser(id);
    }

}
