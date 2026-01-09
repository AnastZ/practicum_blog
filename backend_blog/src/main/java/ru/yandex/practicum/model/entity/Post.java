package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@NamedQueries(value = {
        @NamedQuery(name = "countRecordsForSearchByTitle",
                query = "SELECT count(p) " +
                        " FROM Post p " +
                        " where p.title like CONCAT('%', :searchString, '%') "),
        @NamedQuery(name = "getIdsForSearchByTitle",
                query = "SELECT p.id" +
                        " FROM Post p " +
                        " WHERE p.title LIKE CONCAT('%', :searchString, '%') " +
                        " ORDER BY p.createdDate DESC"),
        @NamedQuery(name = "searchByTitle",
                query = "SELECT p " +
                        " FROM Post p " +
                        " LEFT JOIN FETCH p.tags " +
                        " WHERE p.id IN (:ids) " +
                        " ORDER BY p.createdDate DESC "),
        @NamedQuery(name = "findSinglePost",
                query = " SELECT p " +
                        " FROM Post p " +
                        " LEFT JOIN FETCH p.tags " +
                        " WHERE p.id = :id")


       /* @NamedQuery(name = "searchByTitle",
                query = "SELECT new ru.yandex.practicum.model.dto.PostDTO(p.id, p.title, p.text, p.tags, p.likesCount, COUNT(c)) " +
                        " FROM Post p " +
                        " LEFT JOIN p.tags" +
                        " LEFT JOIN Comment c ON c.post = p " +
                        " WHERE p.title LIKE CONCAT('%', :searchString, '%') " +
                        " GROUP BY p.id " +
                        " ORDER BY p.createdDate DESC "),

        @NamedQuery(name = "findSinglePost",
                query = "SELECT new ru.yandex.practicum.model.dto.PostDTO(p.id, p.title, p.text, p.tags, p.likesCount, COUNT(c)) " +
                        " FROM Post p " +
                        " LEFT JOIN p.tags" +
                        " LEFT JOIN Comment c ON c.post = p " +
                        " WHERE p.id = :id " +
                        " GROUP BY p.id " +
                        " ORDER BY p.createdDate DESC ")    */


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

    @Formula(value = "SELECT COUNT(*) FROM comment c WHERE c.idpost = idpost")
    private Long commentsCount;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "post_tags",
            joinColumns = { @JoinColumn(name = "idpost") },
            inverseJoinColumns = { @JoinColumn(name = "idtag") }
    )
    @OrderBy("name")
    private List<Tag> tags;


    public Long getId() {
        return id;
    }

    protected Post() {
    }
    public Post(@NotNull final String title,
                @NotNull final String text,
                @NotNull final Long likesCount,
                @NotNull final Long commentsCount,
                @NotNull final List<Tag> tags) {
               this.title = title;
        this.text = text;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.tags = tags;
    }
    public Post(@NotNull final Long id,
                @NotNull final String title,
                @NotNull final String text,
                @NotNull final Long likesCount,
                @NotNull final Long commentsCount,
                @NotNull final List<Tag> tags) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.tags = tags;
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

    public @NotNull String getText() {
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", likesCount=" + likesCount +
                ", createdDate=" + createdDate +
                ", tags=" + tags +
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
