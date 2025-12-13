package tests;

import api.AccountApiSteps;
import api.BooksApiSteps;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ProfilePage;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookStoreTests extends TestBase {
    ProfilePage profilePage = new ProfilePage();
    Random random = new Random();
    AccountApiSteps accountApiSteps = new AccountApiSteps();
    BooksApiSteps booksApiSteps = new BooksApiSteps();

    @Test
    @DisplayName("Удалить книгу")
    void deleteBookTest() {
        UserResponseModel registerResponse = accountApiSteps.registerUser();
        AuthResponseModel authResponse = accountApiSteps.authUser();

        String userID = registerResponse.userID();
        String token = authResponse.token();
        String expires = authResponse.expires();

        BooksListResponseModel booksResponse = booksApiSteps.getBooksList();

        List<BookModel> books = booksResponse.books();
        assertTrue(books != null && !books.isEmpty(), "Список книг не должен быть пустым");

        int randomIndex = random.nextInt(books.size());
        BookModel randomBook = books.get(randomIndex);
        String bookName = randomBook.title();
        String bookIsbn = randomBook.isbn();
        AddBookBodyModel addBookData = new AddBookBodyModel(userID, List.of(new IsbnModel(bookIsbn)));

        booksApiSteps.addBookToCart(token, addBookData);

        profilePage
                .openProfilePageWithCookies(userID, expires, token)
                .checkBookIsInList(bookName)
                .deleteFirstBookInList(bookName);
    }
}
