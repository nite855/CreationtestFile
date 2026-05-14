package com.creationtime.controller;

import com.creationtime.domain.collaboration.Comment;
import com.creationtime.domain.collaboration.Post;
import com.creationtime.domain.collaboration.PostCategory;
import com.creationtime.domain.recruitment.RecruitInfo;
import com.creationtime.domain.recruitment.RecruitPost;
import com.creationtime.service.PostService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/teams/{teamId}/posts")
    public Post createPost(@PathVariable Long teamId, @RequestBody CreatePostRequest request) {
        return postService.createPost(teamId, request.authorId(), request.title(), request.content(), request.category());
    }

    @PostMapping("/posts/{postId}")
    public void modifyPost(@PathVariable Long postId, @RequestBody ModifyPostRequest request) {
        postService.modifyPost(postId, request.editorId(), request.title(), request.content(), request.category());
    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId, @RequestBody DeletePostRequest request) {
        postService.deletePost(postId, request.requesterId());
    }

    @PostMapping("/posts/{postId}/comments")
    public Comment writeComment(@PathVariable Long postId, @RequestBody WriteCommentRequest request) {
        return postService.writeComment(postId, request.authorId(), request.content());
    }

    @PostMapping("/teams/{teamId}/recruit-posts")
    public RecruitPost createRecruitPost(@PathVariable Long teamId, @RequestBody CreateRecruitPostRequest request) {
        return postService.createRecruitPost(teamId, request.authorId(), request.title(), request.content(), request.recruitInfo());
    }

    @PostMapping("/recruit-posts/{recruitPostId}")
    public void modifyRecruitPost(@PathVariable Long recruitPostId, @RequestBody ModifyRecruitPostRequest request) {
        postService.modifyRecruitPost(recruitPostId, request.editorId(), request.title(), request.content(), request.recruitInfo());
    }

    public record CreatePostRequest(Long authorId, String title, String content, PostCategory category) {
    }

    public record ModifyPostRequest(Long editorId, String title, String content, PostCategory category) {
    }

    public record DeletePostRequest(Long requesterId) {
    }

    public record WriteCommentRequest(Long authorId, String content) {
    }

    public record CreateRecruitPostRequest(Long authorId, String title, String content, RecruitInfo recruitInfo) {
    }

    public record ModifyRecruitPostRequest(Long editorId, String title, String content, RecruitInfo recruitInfo) {
    }
}
