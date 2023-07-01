package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapperForItem {
    public static CommentDtoForItem mapToDto(Comment comment) {

        return new CommentDtoForItem(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDtoForItem> mapListToDto(List<Comment> comments) {
        List<CommentDtoForItem> commentDtoForItems = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtoForItems.add(mapToDto(comment));
        }
        return commentDtoForItems;
    }

}
