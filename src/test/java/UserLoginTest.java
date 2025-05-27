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

public class UserLoginTest {

    public static String name = RandomStringUtils.randomAlphabetic(10);
    public static String password = RandomStringUtils.randomAlphabetic(10);
    public static String badPassword = RandomStringUtils.randomAlphabetic(10);
    public static String email = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());
    public static String badEmail = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());

    private final UserSteps userSteps = new UserSteps();

    @Before
    public void createUser(){
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        userSteps.userCreate(userCreateRequest);
    }

    @After
    public  void deleteUser() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        userSteps.userDeleteAfterLogin(userLoginRequest);
    }

    @Test
    @DisplayName("Логин существующего пользователем")
    @Description("Проверка логина существующего пользователя")
    public void loginUser() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        userSteps.userLogin(userLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин пользователя с неверным email")
    @Description("Проверка ошибки логина пользователя с неверным email")
    public void loginUserWithBadEmail() {
        UserLoginRequest userBadLoginRequest = new UserLoginRequest(badEmail, password);
        userSteps.userLogin(userBadLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Логин пользователя с неверным password")
    @Description("Проверка ошибки логина пользователя с неверным password")
    public void loginUserWithBadPassword() {
        UserLoginRequest userBadLoginRequest = new UserLoginRequest(email, badPassword);
        userSteps.userLogin(userBadLoginRequest)
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false));
    }
}
