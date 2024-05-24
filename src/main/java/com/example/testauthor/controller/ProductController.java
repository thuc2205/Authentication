package com.example.testauthor.controller;

import com.example.testauthor.Services.ServiceProduct;
import com.example.testauthor.Services.UserService;
import com.example.testauthor.model.LoginDto;
import com.example.testauthor.model.Product;
import com.example.testauthor.model.Role;
import com.example.testauthor.model.User;
import com.example.testauthor.repositories.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {
    @Autowired
    private ServiceProduct serviceProduct;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/product")
    @PreAuthorize("hasRole('ADMIN')")
    public String products(Model model){
        List<Product> list =  serviceProduct.getAllProducts();
        model.addAttribute("products",list);
        return "product";
    };
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login/go")
    @Deprecated
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,
                        HttpServletResponse response, HttpServletRequest request) {
        try {
            String token = userService.login(username, password);

            // Xóa tất cả các cookie cũ trước khi thiết lập cookie mới
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setValue("");
                    cookie.setPath("/"); // Đảm bảo đường dẫn giống với cookie ban đầu
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }

            // Thiết lập cookie mới chứa token
            Cookie tokenCookie = new Cookie("token", token);
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(24 * 60 * 60); // 1 ngày
            tokenCookie.setHttpOnly(false); // Đặt thành false nếu muốn truy cập từ JavaScript
            tokenCookie.setSecure(false); // Đặt thành true nếu trang của bạn sử dụng HTTPS
            response.addCookie(tokenCookie);

            Optional<User> userOptional = userRepo.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getRole().getId() == 1) {
                    return "redirect:/product";
                } else {
                    return "redirect:/registerr";
                }
            } else {
                return "redirect:/login?error=user_not_found";
            }

        } catch (Exception e) {
            return "redirect:/login?error=invalid_credentials";
        }
    }



    @GetMapping("/registerr")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/login";
    }




}
