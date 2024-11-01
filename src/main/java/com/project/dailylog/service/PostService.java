package com.project.dailylog.service;

import com.project.dailylog.model.dto.PostDTO;
import com.project.dailylog.model.entity.Post;
import com.project.dailylog.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;

    @Transactional
    public void postWrite(PostDTO postDTO) throws Exception {
        postRepository.save(Post.builder()
                .userId(postDTO.getUserId())
                .postTitle(postDTO.getPostTitle())
                .postContent(postDTO.getPostContent())
                .postVisible(postDTO.getPostVisible())
                .build());
    }
}
