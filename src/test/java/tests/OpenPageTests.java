package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

public class OpenPageTests extends TestBase {
    @Test
    @DisplayName("Открыть страницу")
    void testt() {
        open("books");
    }
}
