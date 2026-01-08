package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

@NamedQueries(value = {
        @NamedQuery(name = "countRecordsForSearchByTitle",
        query = "SELECT count(p) " +
                " FROM Post p " +
                " where p.title like CONCAT('%', :searchString, '%') "),
        @NamedQuery(name = "searchByTitle",
                query = "SELECT new ru.yandex.practicum.model.dto.PostDTO(p.id, p.title, p.text, p.likesCount, COUNT(c)) " +
                        " FROM Post p " +
                        " LEFT JOIN Comment c ON c.post = p " +
                        " WHERE p.title LIKE CONCAT('%', :searchString, '%') " +
                        " GROUP BY p.id")
})
@Entity
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "idpost", unique = true)
    @NotNull
    private Long id;

    @Column(unique = true, nullable = false, length = 45)
    @NotNull
    @Size(max = 45)
    private String title;

    @Column(nullable = false, length = 2000)
    @NotNull
    @Size(max = 2000)
    private String text;

    @Column(name = "likes_count", nullable = false)
    @NotNull
    @Size(min = 0)
    private Long likesCount;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;
    public Long getId() {
        return id;
    }
    protected Post(){}
    protected Post(@NotNull final Long id,
                   @NotNull final String title,
                   @NotNull final String text,
                   @NotNull final Long likesCount,
                   @NotNull final LocalDate createdDate) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.likesCount = likesCount;
        this.createdDate = createdDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", likesCount=" + likesCount +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) && Objects.equals(title, post.title) && Objects.equals(text, post.text) && Objects.equals(likesCount, post.likesCount) && Objects.equals(createdDate, post.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, text, likesCount, createdDate);
    }
}
