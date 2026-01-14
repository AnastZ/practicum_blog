package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
                        " WHERE p.id = :id"),
        @NamedQuery(name="incrementLikes",
        query = "UPDATE Post p SET p.likesCount = p.likesCount + 1 WHERE p.id = :id"),
        @NamedQuery(name = "getCountLikes",
        query = "SELECT p.likesCount FROM Post p WHERE p.id = :id"),
        @NamedQuery(name = "getImagePath",
        query = "SELECT p.imagePath FROM Post p WHERE p.id = :id")
})
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "idpost", unique = true)
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
    @Min(value = 0, message = "Число лайков не может быть ниже нуля.")
    @NotNull
    private Long likesCount;

    @Column(name = "created_date", nullable = false)
    @NotNull
    private LocalDate createdDate;

    @Formula(value = "(SELECT COUNT(*) FROM comment c WHERE c.idpost = idpost)")
    private Long commentsCount;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "post_tags",
            joinColumns = { @JoinColumn(name = "idpost") },
            inverseJoinColumns = { @JoinColumn(name = "idtag") }
    )
    @OrderBy("name")
    private List<Tag> tags;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    public Long getId() {
        return id;
    }

    protected Post() {
    }
    public Post(@NotNull final String title,
                @NotNull final String text,
                @NotNull final List<Tag> tags) {

        this.title = title;
        this.text = text;
        this.tags = tags;
        this.likesCount = 0L;
        this.createdDate = LocalDate.now();
        this.commentsCount = 0L;
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

    public void setCommentsCount(Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
