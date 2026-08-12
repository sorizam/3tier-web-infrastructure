package com.arch1.webapp.controller;

import com.arch1.webapp.entity.User;
import com.arch1.webapp.service.PostService;
import com.arch1.webapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final UserService userService;
    private final PostService postService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 대시보드 통계
        long userPostCount = postService.getPostCountByAuthor(currentUser);
        long totalUsers = userService.findAllUsers().size();
        long adminCount = userService.getAdminCount();
        
        // 최근 게시글
        var recentPosts = postService.getRecentPosts();
        var popularPosts = postService.findPopularPosts(0, 5);
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userPostCount", userPostCount);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("recentPosts", recentPosts);
        model.addAttribute("popularPosts", popularPosts.getContent());
        
        return "dashboard/index";
    }
}
