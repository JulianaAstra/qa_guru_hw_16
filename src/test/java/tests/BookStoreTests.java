package tests;

import io.restassured.response.Response;
import models.AuthResponseModel;
import models.BookModel;
import models.BooksListResponseModel;
import models.UserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static specs.Spec.requestSpec;
import static specs.Spec.responseSpec;

public class BookStoreTests extends TestBase {
    @Test
    @DisplayName("Удалить книгу")
    void deleteBookTest() {
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
        String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
                registerResponse.userID() , bookIsbn);

        step("Make add book to user cart request", () ->
            given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + token)
                    .body(bookData)
                    .when()
                    .post("/BookStore/v1/Books")
                    .then()
                    .spec(responseSpec(201))
        );

        open("favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", registerResponse.userID()));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));

        open("profile");
        $(".ReactTable").shouldHave(text(bookName));
    }
}
