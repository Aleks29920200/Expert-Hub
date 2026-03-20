package com.example.skillsh.domain.dto.review;

import java.time.LocalDateTime;

public class ReviewCommentDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime created;

    // Добави Getters и Setters за тези 4 полета
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
}
