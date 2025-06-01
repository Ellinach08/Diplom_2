import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.models.UserCreateRequest;
import ru.praktikum.models.UserLoginRequest;
import ru.praktikum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    private final String name = RandomStringUtils.randomAlphabetic(10);
    private final String password = RandomStringUtils.randomAlphabetic(10);
    private final String badPassword = RandomStringUtils.randomAlphabetic(10);
    private final String email = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());
    private final String badEmail = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());
    private final String userUnauthorizedMessage = "email or password are incorrect";

    private final UserSteps userSteps = new UserSteps();
    private String accessToken;
    private ValidatableResponse userResponse;

    @Before
    public void createUser(){
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        userResponse = userSteps.userCreate(userCreateRequest);
        accessToken = userResponse.extract().path("accessToken");
    }

    @After
    public  void deleteUser() {
        userSteps.userDelete(accessToken);
    }

    @Test
    @DisplayName("Логин существующего пользователем")
    @Description("Проверка логина существующего пользователя")
    public void loginUserTest() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        userSteps.userLogin(userLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин пользователя с неверным email")
    @Description("Проверка ошибки логина пользователя с неверным email")
    public void loginUserWithBadEmailTest() {
        UserLoginRequest userBadLoginRequest = new UserLoginRequest(badEmail, password);
        userSteps.userLogin(userBadLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(userUnauthorizedMessage));
    }

    @Test
    @DisplayName("Логин пользователя с неверным password")
    @Description("Проверка ошибки логина пользователя с неверным password")
    public void loginUserWithBadPasswordTest() {
        UserLoginRequest userBadLoginRequest = new UserLoginRequest(email, badPassword);
        userSteps.userLogin(userBadLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(userUnauthorizedMessage));
    }
}
