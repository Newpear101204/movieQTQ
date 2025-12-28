package com.movie.movie.filter;


import com.movie.movie.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.data.util.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil, @Lazy UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        try {
//            if( isBypassToken(request) ) {
////            if( true ) {
//                filterChain.doFilter(request, response); //enable bypass
//                return;
//            }
//            final String authHeader = request.getHeader("Authorization");
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//                return;
//            }
//            final String token = authHeader.substring(7);
//            final String username = jwtTokenUtil.extractPhoneNumber(token);
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                User userDetails = (User) userDetailsService.loadUserByUsername(username);
//                if(jwtTokenUtil.validateToken(token, userDetails)) {
//                    UsernamePasswordAuthenticationToken authenticationToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null,
//                                    userDetails.getAuthorities());
//                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                    filterChain.doFilter(request, response); //enable bypass
//                }
//            }
//            //    filterChain.doFilter(request, response); //enable bypass
//        }catch (Exception e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//        }
//
//    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        try {
//            final String authHeader = request.getHeader("Authorization");
//
//            // 1. Ưu tiên kiểm tra Token trước (kể cả URL public hay private)
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                final String token = authHeader.substring(7);
//                final String username = jwtTokenUtil.extractPhoneNumber(token);
//
//                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    User userDetails = (User) userDetailsService.loadUserByUsername(username);
//                    if (jwtTokenUtil.validateToken(token, userDetails)) {
//                        UsernamePasswordAuthenticationToken authenticationToken =
//                                new UsernamePasswordAuthenticationToken(userDetails, null,
//                                        userDetails.getAuthorities());
//                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                        // QUAN TRỌNG: Set User vào Context tại đây
//                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                    }
//                }
//            }
//            // 2. Nếu KHÔNG có token, thì mới kiểm tra xem có được bypass không
//            else if (isBypassToken(request)) {
//                // Cho qua, không làm gì cả -> SecurityContext sẽ rỗng -> Anonymous User
//            }
//            // 3. Không có token và cũng không được bypass -> Lỗi
//            else {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//                return;
//            }
//
//            // Tiếp tục chuỗi filter
//            filterChain.doFilter(request, response);
//
//        } catch (Exception e) {
//            // Có thể log lỗi ra console để debug nếu token sai định dạng
//            System.out.println("Error JWT Filter: " + e.getMessage());
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//        }
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");

            // 1. Chỉ xử lý logic xác thực NẾU CÓ TOKEN
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                final String token = authHeader.substring(7);
                final String username = jwtTokenUtil.extractPhoneNumber(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User userDetails = (User) userDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null,
                                        userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set User vào Context (Đã đăng nhập thành công)
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
            // 2. NẾU KHÔNG CÓ TOKEN:
            // Không làm gì cả. SecurityContext vẫn null (nghĩa là Anonymous User/Khách).
            // Code sẽ trôi xuống dòng filterChain.doFilter bên dưới.

        } catch (Exception e) {
            // Log lỗi nếu token sai định dạng, nhưng KHÔNG throw exception để request không chết
            System.err.println("Cannot set user authentication: " + e.getMessage());
        }

        // 3. QUAN TRỌNG NHẤT: Luôn luôn cho phép request đi tiếp!
        // - Nếu request vào API public (/movie/view/1): SecurityConfig thấy permitAll -> Cho qua -> Controller.
        // - Nếu request vào API private (/user/history): SecurityConfig thấy cần authen mà Context null -> Chặn (403).
        filterChain.doFilter(request, response);
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of("/user/login", "POST"),
                Pair.of("/user/register", "POST"),
                Pair.of("/movie/search", "POST"),
                Pair.of("/user/person/{slug}", "GET"),
                Pair.of("/movie/view/{id}", "POST")
        );
        String first = request.getServletPath();
        String  second = request.getMethod();
        for(Pair<String, String> bypassToken: bypassTokens) {
            String x = bypassToken.getFirst();
            String y = bypassToken.getSecond();
            if (request.getServletPath().equals(bypassToken.getFirst()) &&
                    request.getMethod().equals(bypassToken.getSecond())){
                return true;
            }
        }
        return false;
    }
}
