package com.arch1.webapp.controller;

import com.arch1.webapp.entity.Post;
import com.arch1.webapp.entity.User;
import com.arch1.webapp.service.PostService;
import com.arch1.webapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    
    private final PostService postService;
    private final UserService userService;
    
    @GetMapping
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String search,
                           Model model) {
        
        Page<Post> posts;
        if (search != null && !search.trim().isEmpty()) {
            posts = postService.searchPosts(search, page, size);
            model.addAttribute("search", search);
        } else {
            posts = postService.findAllPublishedPosts(page, size);
        }
        
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        
        return "posts/list";
    }
    
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        postService.incrementViewCount(id);
        
        model.addAttribute("post", post);
        return "posts/view";
    }
    
    @GetMapping("/create")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/create";
    }
    
    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        post.setAuthor(currentUser);
        postService.createPost(post);
        
        redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 작성되었습니다.");
        return "redirect:/posts";
    }
    
    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        model.addAttribute("post", post);
        return "posts/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable Long id, @ModelAttribute Post postDetails, 
                          RedirectAttributes redirectAttributes) {
        try {
            postService.updatePost(id, postDetails);
            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 수정되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/posts/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/posts";
    }
}
