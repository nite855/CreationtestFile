package com.creationtime.service;

import com.creationtime.domain.collaboration.Comment;
import com.creationtime.domain.collaboration.Post;
import com.creationtime.domain.collaboration.PostCategory;
import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.recruitment.RecruitPost;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.team.TeamCategory;
import com.creationtime.domain.user.User;
import com.creationtime.repository.PostRepository;
import com.creationtime.repository.RecruitPostRepository;
import com.creationtime.repository.TeamRepository;
import com.creationtime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RecruitPostRepository recruitPostRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(Long teamId, Long authorId, String title, String content, PostCategory category) {
        Post post = new Post(title, content, category, findUser(authorId), findTeam(teamId));
        return postRepository.save(post);
    }

    public void modifyPost(Long postId, Long editorId, String title, String content, PostCategory category) {
        Post post = findPost(postId);
        post.modify(findUser(editorId), title, content, category);
        postRepository.save(post);
    }

    public void deletePost(Long postId, Long requesterId) {
        Post post = findPost(postId);
        if (!post.isWrittenBy(findUser(requesterId))) {
            post.team().assertLeader(findUser(requesterId));
        }
        postRepository.deleteById(postId);
    }

    public Comment writeComment(Long postId, Long authorId, String content) {
        Post post = findPost(postId);
        Comment comment = post.writeComment(findUser(authorId), content);
        postRepository.save(post);
        return comment;
    }

    public RecruitPost createRecruitPost(
            Long teamId,
            Long authorId,
            String title,
            String content,
            TeamCategory category,
            String recruitField,
            String techStack,
            String requiredRole,
            int recruitCount,
            String progressMethod,
            String contactLink,
            LocalDateTime deadline
    ) {
        RecruitPost recruitPost = new RecruitPost(
                title,
                content,
                category,
                recruitField,
                techStack,
                requiredRole,
                recruitCount,
                progressMethod,
                contactLink,
                deadline,
                findUser(authorId),
                findTeam(teamId)
        );
        return recruitPostRepository.save(recruitPost);
    }

    public void modifyRecruitPost(
            Long recruitPostId,
            Long editorId,
            String title,
            String content,
            TeamCategory category,
            String recruitField,
            String techStack,
            String requiredRole,
            int recruitCount,
            String progressMethod,
            String contactLink,
            LocalDateTime deadline
    ) {
        RecruitPost recruitPost = recruitPostRepository.findById(recruitPostId)
                .orElseThrow(() -> new DomainException("recruit post not found."));
        recruitPost.modify(findUser(editorId), title, content, category, recruitField, techStack, requiredRole, recruitCount, progressMethod, contactLink, deadline);
        recruitPostRepository.save(recruitPost);
    }

    @Transactional(readOnly = true)
    public Post findPost(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new DomainException("post not found."));
    }
    
    @Transactional(readOnly = true)
    public List<Post> findPostsByTeam(Long teamId) {
        return postRepository.findByTeamId(teamId);
    }
    
    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new DomainException("post not found."));
    }

    private Team findTeam(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new DomainException("team not found."));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("user not found."));
    }
}
