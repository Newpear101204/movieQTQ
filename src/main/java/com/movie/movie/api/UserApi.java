package com.movie.movie.api;

import com.cloudinary.api.ApiResponse;
import com.movie.movie.model.dto.ChangePasswordDTO;
import com.movie.movie.model.dto.LoginDTO;
import com.movie.movie.model.dto.RegisterDTO;
import com.movie.movie.model.dto.UpdateUserDTO;
import com.movie.movie.model.response.*;
import com.movie.movie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/")
@CrossOrigin(origins = "http://localhost:3000")
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

    @Autowired
    private PersonsService personsService;
    @Autowired
    private WebClient.Builder builder;

    // đăng nhập
    @PostMapping("/login")
    public LoginResponse login (@RequestBody LoginDTO loginDTO) {
        return usersService.login(loginDTO);
    }

//    @PostMapping("/register")
//    public void register (@RequestBody RegisterDTO registerDTO) {
//        usersService.register(registerDTO);
//    }

    // đăng ký
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        usersService.register(registerDTO);
        return ResponseEntity.ok(Map.of("message", "Register success"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO request) {
        usersService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Update success"));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateUserDTO updateUserDTO){
        usersService.updateUser(updateUserDTO);
        return ResponseEntity.ok(Map.of("message", "Update success"));
    }

    @GetMapping("/history")
    public List<MovieResponse> getHistory(){
        return movieService.getHistory();
    }

    @PostMapping("/history/{id}")
    public ResponseEntity<String> addHistory(@PathVariable Long id) {
        watchHistoryService.addToHistory(id);
        return ResponseEntity.ok("Đã thêm vào lịch sử"); // Trả về 200 OK
    }

//    @PostMapping("/history/{id}")
//    public void addHistory(@PathVariable Long id){
//        watchHistoryService.addToHistory(id);
//    }

    // them danh sach phim yêu thích
    @PostMapping("/watch-list/{id}")
    public void addToWatchList(@PathVariable Long id){
        userWatchListService.addToWatchList(id);
    }

    // lay danh sach phim yeu thich
//    @GetMapping("/watch-list")
//    public void getWatchList(){
//        usersService.movieOfWatchList();
//    }
    @GetMapping("/watch-list")
    public List<MovieResponse> getWatchList(){
        return usersService.movieOfWatchList();
    }

    // xoa phim khoi danh sach yeu thich
    @DeleteMapping("/watch-list/{id}")
    public ResponseEntity<String> removeFromWatchList(@PathVariable Long id) {
        userWatchListService.removeFromWatchList(id);
        return ResponseEntity.ok("Đã xóa phim khỏi danh sách yêu thích");
    }

    // lay chi tiet dien vien theo slug
    @GetMapping("/person/{slug}")
    public ResponseEntity<PersonDetailResponse> getPersonBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(personsService.getPersonDetail(slug));
    }

    // lay danh sach dien vien yeu thich
    @GetMapping("/actor")
    public List<CastResponse> getActor(){
       return usersService.castOfWatchList();
    }

    // them dien vien yeu thich
    @PostMapping("/actor/{id}")
    public void addActorToWatchList(@PathVariable Long id){
        userActorListService.addToActorList(id);
    }

    // xoa dien vien khoi danh sach yeu thich
    @DeleteMapping("/actor/{id}")
    public ResponseEntity<String> removeActorFromList(@PathVariable Long id) {
        userActorListService.removeActorFromList(id);
        return ResponseEntity.ok("Đã xóa diễn viên khỏi danh sách yêu thích");
    }

    @GetMapping
    public List<UsersResponse> getUser(){
        return usersService.allUsers();
    }

    @GetMapping("/profile")
    public UsersResponse getMyProfile() {
        return usersService.getMyInfo();
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id ){
        usersService.deleteUser(id);
    }

}
