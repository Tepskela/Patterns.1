package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;


class DeliveryTest {
    SelenideElement city = $("[data-test-id = 'city'] input");
    SelenideElement date = $("[data-test-id = 'date'] input");
    SelenideElement phone = $("[data-test-id = 'phone'] input");
    SelenideElement name = $("[data-test-id = 'name'] input");
    SelenideElement checkbox = $("[data-test-id = 'agreement']");
    SelenideElement scheduleButton = $(".button__text");
    SelenideElement rescheduleButton = $("[data-test-id = 'replan-notification'] .notification__content button");
    SelenideElement successPopUp = $("[data-test-id = 'success-notification'] .notification__content");
    SelenideElement reschedulePopUp = $("[data-test-id = 'replan-notification'] .notification__content");

    String successMessage = "Встреча успешно запланирована на ";
    String warningMessage = "У вас уже запланирована встреча на другую дату. Перепланировать?";


    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }
    @AfterAll
        static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }
        @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        city.setValue(validUser.getCity());
        date.sendKeys(Keys.chord(Keys.CONTROL,"a"), Keys.BACK_SPACE);
        date.setValue(firstMeetingDate);
        name.setValue(validUser.getName());
        phone.setValue(validUser.getPhone());
        checkbox.click();
        scheduleButton.click();
        successPopUp
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text(successMessage + firstMeetingDate));

        date.sendKeys(Keys.chord(Keys.CONTROL,"a"), Keys.BACK_SPACE);
        date.setValue(secondMeetingDate);
        scheduleButton.click();
        reschedulePopUp
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text(warningMessage));
        rescheduleButton.click();
        successPopUp
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text(successMessage + secondMeetingDate));
    }
}