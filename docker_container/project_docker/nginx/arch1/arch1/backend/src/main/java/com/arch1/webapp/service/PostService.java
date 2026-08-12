package com.arch1.webapp.service;

import com.arch1.webapp.entity.Post;
import com.arch1.webapp.entity.User;
import com.arch1.webapp.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    
    private final PostRepository postRepository;
    
    public Post createPost(Post post) {
        return postRepository.save(post);
    }
    
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
    
    public Page<Post> findAllPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByStatusOrderByCreatedAtDesc(Post.PostStatus.PUBLISHED, pageable);
    }
    
    public Page<Post> findByAuthor(User author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByAuthorOrderByCreatedAtDesc(author, pageable);
    }
    
    public Page<Post> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByKeyword(keyword, pageable);
    }
    
    public Page<Post> findPopularPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findPopularPosts(pageable);
    }
    
    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setStatus(postDetails.getStatus());
        
        return postRepository.save(post);
    }
    
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        post.setStatus(Post.PostStatus.ARCHIVED);
        postRepository.save(post);
    }
    
    public void incrementViewCount(Long postId) {
        postRepository.incrementViewCount(postId);
    }
    
    public long getPostCountByAuthor(User author) {
        return postRepository.countByAuthor(author);
    }
    
    public List<Post> getRecentPosts() {
        return postRepository.findTop5ByStatusOrderByCreatedAtDesc(Post.PostStatus.PUBLISHED);
    }
}
