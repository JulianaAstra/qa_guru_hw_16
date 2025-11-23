package tests;

import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ProfilePage;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static specs.Spec.requestSpec;
import static specs.Spec.responseSpec;

public class BookStoreTests extends TestBase {
    @Test
    @DisplayName("Удалить книгу")
    void deleteBookTest() {
        ProfilePage profilePage = new ProfilePage();
        TestData testData = new TestData();
        Random random = new Random();

        UserResponseModel registerResponse = step("Make register new user request", () ->
                given()
                .spec(requestSpec)
                .body(testData.authData)
                .when()
                .post("/Account/v1/User")
                .then()
                .spec(responseSpec(201))
                .extract().as(UserResponseModel.class));

        AuthResponseModel authResponse = step("Make auth user request", () ->
                await().atMost(20, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                            AuthResponseModel response = given()
                                    .spec(requestSpec)
                                    .body(testData.authData)
                                    .when()
                                    .post("/Account/v1/GenerateToken")
                                    .then()
                                    .spec(responseSpec(200))
                                    .extract().as(AuthResponseModel.class);

                            return "Success".equals(response.status()) && response.token() != null ? response : null;
                        },
                Objects::nonNull));

        String userID = registerResponse.userID();
        String token = authResponse.token();
        String expires = authResponse.expires();

        BooksListResponseModel booksResponse = step("Make get books list request", () ->
                given()
                .spec(requestSpec)
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .spec(responseSpec(200))
                .extract().as(BooksListResponseModel.class));

        List<BookModel> books = booksResponse.books();
        assertTrue(books != null && !books.isEmpty(), "Список книг не должен быть пустым");

        int randomIndex = random.nextInt(books.size());
        BookModel randomBook = books.get(randomIndex);
        String bookName = randomBook.title();
        String bookIsbn = randomBook.isbn();
        AddBookBodyModel addBookData = new AddBookBodyModel(userID, List.of(new IsbnModel(bookIsbn)));

        step("Make add book to user cart request", () ->
            given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + token)
                    .body(addBookData)
                    .when()
                    .post("/BookStore/v1/Books")
                    .then()
                    .spec(responseSpec(201))
        );

        profilePage
                .openProfilePageWithCookies(userID, expires, token)
                .checkBookIsInList(bookName)
                .deleteFirstBookInList(bookName);
    }
}
