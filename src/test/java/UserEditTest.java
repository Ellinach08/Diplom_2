import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
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

    public static String name = RandomStringUtils.randomAlphabetic(10);
    public static String password = RandomStringUtils.randomAlphabetic(10);
    public static String email = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());

    public static String editName = RandomStringUtils.randomAlphabetic(10);
    public static String editPassword = RandomStringUtils.randomAlphabetic(10);
    public static String editEmail = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());

    private final UserSteps userSteps = new UserSteps();
    private static String delEmail, delPassword;

    @Before
    public void createUser(){
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        userSteps.userCreate(userCreateRequest);
    }

    @After
    public  void deleteUser() {
        UserSteps userSteps = new UserSteps();
        UserLoginRequest userLoginRequest = new UserLoginRequest(delEmail, delPassword);
        userSteps.userDeleteAfterLogin(userLoginRequest);
    }

    @Test
    @DisplayName("Обновление email пользователя с авторизацией")
    @Description("Проверка обновления email пользователя с авторизацией")
    public void editUserEmailWithAuthorization() {
        UserCreateRequest userEditRequest = new UserCreateRequest(editEmail, password, name);
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        userSteps.userEditAfterLogin(userLoginRequest, userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(editEmail));

        delEmail = editEmail;
        delPassword = password;
    }
    @Test
    @DisplayName("Обновление email пользователя без авторизации")
    @Description("Проверка ошибки обновления email пользователя без авторизации")
    public void editUserEmailWithoutAuthorization() {
        UserCreateRequest userEditRequest = new UserCreateRequest(editEmail, password, name);
        UserSteps userSteps = new UserSteps();
        userSteps.userEdit(userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false));

        delEmail = email;
        delPassword = password;
    }
    @Test
    @DisplayName("Обновление password пользователя с авторизацией")
    @Description("Проверка обновления password пользователя с авторизацией")
    public void editUserPasswordWithAuthorization() {
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

        delEmail = email;
        delPassword = editPassword;
    }

    @Test
    @DisplayName("Обновление password пользователя без авторизации")
    @Description("Проверка ошибки обновления password пользователя без авторизации")
    public void editUserPasswordWithoutAuthorization() {
        UserCreateRequest userEditRequest = new UserCreateRequest(email, editPassword, name);
        userSteps.userEdit(userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false));

        delEmail = email;
        delPassword = password;
    }

    @Test
    @DisplayName("Обновление name пользователя с авторизацией")
    @Description("Проверка обновления name пользователя с авторизацией")
    public void editUserNameWithAuthorization() {
        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, editName);
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        UserSteps userSteps = new UserSteps();
        userSteps.userEditAfterLogin(userLoginRequest, userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(editName));

        delEmail = email;
        delPassword = password;
    }
    @Test
    @DisplayName("Обновление name пользователя без авторизации")
    @Description("Проверка ошибки обновления name пользователя без авторизации")
    public void editUserNameWithoutAuthorization() {
        UserCreateRequest userEditRequest = new UserCreateRequest(email, password, editName);
        userSteps.userEdit(userEditRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false));

        delEmail = email;
        delPassword = password;
    }

}
