package com.arch1.webapp.config;

import com.arch1.webapp.entity.Post;
import com.arch1.webapp.entity.User;
import com.arch1.webapp.repository.PostRepository;
import com.arch1.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // 초기 사용자 생성
        if (userRepository.count() == 0) {
            createInitialUsers();
        }
        
        // 초기 게시글 생성
        if (postRepository.count() == 0) {
            createInitialPosts();
        }
    }
    
    private void createInitialUsers() {
        // 관리자 사용자
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@arch1.com");
        admin.setFullName("관리자");
        admin.setRole(User.UserRole.ADMIN);
        userRepository.save(admin);
        
        // 일반 사용자
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("user123"));
        user1.setEmail("user1@arch1.com");
        user1.setFullName("홍길동");
        user1.setRole(User.UserRole.USER);
        userRepository.save(user1);
        
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("user123"));
        user2.setEmail("user2@arch1.com");
        user2.setFullName("김철수");
        user2.setRole(User.UserRole.USER);
        userRepository.save(user2);
    }
    
    private void createInitialPosts() {
        User admin = userRepository.findByUsername("admin").orElse(null);
        User user1 = userRepository.findByUsername("user1").orElse(null);
        User user2 = userRepository.findByUsername("user2").orElse(null);
        
        if (admin != null) {
            Post post1 = new Post();
            post1.setTitle("Arch1 웹 애플리케이션에 오신 것을 환영합니다!");
            post1.setContent("이 게시판은 3티어 아키텍처로 구성된 Arch1 웹 애플리케이션의 일부입니다. " +
                           "Spring Boot, Thymeleaf, JPA를 사용하여 구현되었으며, " +
                           "사용자 인증, 게시판 기능 등을 제공합니다.");
            post1.setAuthor(admin);
            post1.setViewCount(15);
            postRepository.save(post1);
            
            Post post2 = new Post();
            post2.setTitle("3티어 아키텍처의 장점");
            post2.setContent("3티어 아키텍처는 프레젠테이션 계층, 비즈니스 로직 계층, 데이터 계층으로 분리되어 " +
                           "확장성, 유지보수성, 보안성을 제공합니다. " +
                           "각 계층이 독립적으로 개발되고 배포될 수 있어 개발 효율성을 높일 수 있습니다.");
            post2.setAuthor(admin);
            post2.setViewCount(23);
            postRepository.save(post2);
        }
        
        if (user1 != null) {
            Post post3 = new Post();
            post3.setTitle("Spring Boot 개발 팁");
            post3.setContent("Spring Boot를 사용할 때 유용한 팁들을 공유합니다. " +
                           "1. 자동 설정을 활용하세요. 2. 프로파일을 사용하여 환경별 설정을 관리하세요. " +
                           "3. Actuator를 사용하여 애플리케이션 모니터링을 하세요.");
            post3.setAuthor(user1);
            post3.setViewCount(8);
            postRepository.save(post3);
        }
        
        if (user2 != null) {
            Post post4 = new Post();
            post4.setTitle("Tailwind CSS 사용법");
            post4.setContent("Tailwind CSS는 유틸리티 퍼스트 CSS 프레임워크입니다. " +
                           "미리 정의된 클래스들을 조합하여 빠르게 스타일링할 수 있습니다. " +
                           "반응형 디자인도 쉽게 구현할 수 있어 현대적인 웹 개발에 매우 유용합니다.");
            post4.setAuthor(user2);
            post4.setViewCount(12);
            postRepository.save(post4);
        }
    }
}
