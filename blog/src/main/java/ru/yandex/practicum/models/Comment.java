package ru.practicum.blog.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@NamedQueries({

        @NamedQuery(name = "getCommentByIdAndPostId",
        query = "SELECT c FROM Comment c WHERE c.id = :commentId AND c.post.id = :postId")
})
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcomment", unique = true)
    private Long id;

    @NotNull
    private String text;

    @NotNull
    @JoinColumn(name = "idpost", nullable = false, updatable = false)
    @ManyToOne(targetEntity = Post.class, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Post post;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    protected Comment(){}

    public Comment(@NotNull final String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(text, comment.text) && Objects.equals(post, comment.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, post);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", post=" + post +
                '}';
    }
}
