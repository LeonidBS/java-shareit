package ru.practicum.shareit.comment.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.model.Comment;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentsInMemoryRepository {
    private final Map<Integer, Comment> comments = new HashMap<>();
    private int id = 1;

    public Comment save(Comment comment) {

        if (comment.getId() == null) {
            comment.setId(id++);
        }

        comments.put(comment.getId(), comment);

        return comment;
    }

}
