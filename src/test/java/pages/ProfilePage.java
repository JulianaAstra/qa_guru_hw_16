package pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class ProfilePage {
    SelenideElement usersBooksList = $(".ReactTable");
    SelenideElement deleteFirstBookInListBtn = $(".rt-tbody .rt-tr-group #delete-record-undefined");
    SelenideElement modalWindow = $(".modal-content");
    SelenideElement okBtnInModal = $("#closeSmallModal-ok");

    @Step
    @DisplayName("Открыть профиль пользователя {userId}")
    public ProfilePage openProfilePageWithCookies(String userId, String expires, String token) {
        open("favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));

        open("profile");
        return this;
    }

    @Step
    @DisplayName("Книга {bookName} присутствует в списке книг пользователя")
    public ProfilePage checkBookIsInList(String bookName) {
        usersBooksList.shouldHave(text(bookName));
        return this;
    }

    @Step
    @DisplayName("Удалить книгу {bookName} из списка")
    public ProfilePage deleteFirstBookInList(String bookName) {
        deleteFirstBookInListBtn.shouldBe(visible)
                .click();
        confirmActionInModalWindow();
        confirmActionInBrowser();
        usersBooksList.shouldNotHave(text(bookName));

        return this;
    }

    @Step
    @DisplayName("Подвердить действие в модальном окне")
    public void confirmActionInModalWindow() {
        modalWindow.shouldBe(visible);
        okBtnInModal.shouldBe(visible)
                .click();
        modalWindow.shouldNotBe(visible);
    }

    @Step
    @DisplayName("Подвердить действие в браузере")
    public void confirmActionInBrowser() {
        Selenide.confirm();
    }
}
