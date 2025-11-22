package models;

public record BookModel(
        String isbn,
        String title,
        String subTitle,
        String author,
        String publish_date,
        String publisher,
        String pages,
        String description,
        String website) {
}
