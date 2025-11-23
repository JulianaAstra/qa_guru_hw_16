package models;

import java.util.List;

public record AddBookBodyModel(
        String userId,
        List<IsbnModel> collectionOfIsbns) {}
