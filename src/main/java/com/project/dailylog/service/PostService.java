package com.project.dailylog.service;

import com.project.dailylog.model.dto.PostDTO;
import com.project.dailylog.model.entity.Post;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.repository.PostRepository;
import com.project.dailylog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;

    @Transactional
    public void postWrite(PostDTO postDTO) throws Exception {
        postRepository.save(Post.builder()
                .userId(postDTO.getUserId())
                .postTitle(postDTO.getPostTitle())
                .postContent(postDTO.getPostContent())
                .postVisible(postDTO.getPostVisible())
                .build());
    }

    @Transactional
    public List<PostDTO> getAllPost() throws Exception {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")
                .and(Sort.by(Sort.Direction.ASC, "lastUpdatedAt")));;
        return posts.stream()
                .map(PostDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostDTO> getPostsByUser(String userEmail) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        User user = optionalUser.orElseThrow(() -> new NoSuchElementException("User not found"));
        List<Post> posts = postRepository.findByUserId(user.getId());
        return posts.stream()
                .map(PostDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
