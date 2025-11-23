package models;

import java.util.List;

public record UserResponseModel(
        String userID,
        String username,
        List<BookModel> books){}