package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByItemId(Integer itemId);

    @Modifying
    @Query("DELETE FROM Comment c " +
            "WHERE c.author.id = ?1 ")
    void deleteCommentsByUserId(Integer userId);

}
