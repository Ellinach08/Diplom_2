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

public class UserEditTest {

    private final String name = RandomStringUtils.randomAlphabetic(10);
    private final String password = RandomStringUtils.randomAlphabetic(10);
    private final String email = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());

    private final String editName = RandomStringUtils.randomAlphabetic(10);
    private final String editPassword = RandomStringUtils.randomAlphabetic(10);
    private final String editEmail = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());

    private final String userUnauthorizedMessage = "You should be authorised";

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
    @DisplayName("Обновление email пользователя с авторизацией")
    @Description("Проверка обновления email пользователя с авторизацией")
    public void editUserEmailWithAuthorizationTest() {
        UserCreateRequest userEditRequest = new UserCreateRequest(editEmail, password, name);
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        userSteps.userEditAfterLogin(userLoginRequest, userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(editEmail));
    }

    @Test
    @DisplayName("Обновление email пользователя без авторизации")
    @Description("Проверка ошибки обновления email пользователя без авторизации")
    public void editUserEmailWithoutAuthorizationTest() {
        UserCreateRequest userEditRequest = new UserCreateRequest(editEmail, password, name);
        UserSteps userSteps = new UserSteps();
        userSteps.userEdit(userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(userUnauthorizedMessage));
    }

    @Test
    @DisplayName("Обновление password пользователя с авторизацией")
    @Description("Проверка обновления password пользователя с авторизацией")
    public void editUserPasswordWithAuthorizationTest() {
        UserCreateRequest userEditRequest = new UserCreateRequest(email, editPassword, name);
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        UserLoginRequest userNewLoginRequest = new UserLoginRequest(email, editPassword);
        userSteps.userEditAfterLogin(userLoginRequest, userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));

        userSteps.userLogin(userNewLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Обновление password пользователя без авторизации")
    @Description("Проверка ошибки обновления password пользователя без авторизации")
    public void editUserPasswordWithoutAuthorizationTest() {
        UserCreateRequest userEditRequest = new UserCreateRequest(email, editPassword, name);
        userSteps.userEdit(userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(userUnauthorizedMessage));
    }

    @Test
    @DisplayName("Обновление name пользователя с авторизацией")
    @Description("Проверка обновления name пользователя с авторизацией")
    public void editUserNameWithAuthorizationTest() {
        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, editName);
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        UserSteps userSteps = new UserSteps();
        userSteps.userEditAfterLogin(userLoginRequest, userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(editName));
    }

    @Test
    @DisplayName("Обновление name пользователя без авторизации")
    @Description("Проверка ошибки обновления name пользователя без авторизации")
    public void editUserNameWithoutAuthorizationTest() {
        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, editName);
        userSteps.userEdit(userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(userUnauthorizedMessage));
    }

}
