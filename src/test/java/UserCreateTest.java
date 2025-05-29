import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.models.UserCreateRequest;
import ru.praktikum.models.UserLoginRequest;
import ru.praktikum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreateTest {

    private final String name = RandomStringUtils.randomAlphabetic(10);
    private final String password = RandomStringUtils.randomAlphabetic(10);
    private final String email = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(5).toLowerCase(),
            RandomStringUtils.randomAlphabetic(3).toLowerCase());

    private final UserSteps userSteps = new UserSteps();
    private final String userExistsMessage = "User already exists";
    private final String requiredFieldsMessage = "Email, password and name are required fields";

    @After
    public void deleteUser() {
        try {
            UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
            userSteps.userDeleteAfterLogin(userLoginRequest);
        } catch (IllegalArgumentException e){
            System.out.println("Удаление пользователя не требуется");
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создания нового уникального пользователя")
    public void createNewUserTest() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(email, password, name);
        userSteps.userCreate(userCreateRequest)
                .assertThat().statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание зарегистрированного ранее пользователя")
    @Description("Проверка ошибки создания зарегистрированного ранее пользователя")
    public void createDuplicateUserTest() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, name);
        userSteps.userCreate(userCreateAndEditRequest);
        userSteps.userCreate(userCreateAndEditRequest)
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(userExistsMessage));
    }

    @Test
    @DisplayName("Создание пользователя без поля email")
    @Description("Проверка ошибки создания пользователя без поля email")
    public void createUserWithoutEmailTest() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(null, password, name);
        userSteps.userCreate(userCreateAndEditRequest)
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(requiredFieldsMessage));
    }

    @Test
    @DisplayName("Создание пользователя без поля password")
    @Description("Проверка ошибки создания пользователя без поля password")
    public void createUserWithoutPasswordTest() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, null, name);
        userSteps.userCreate(userCreateAndEditRequest)
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(requiredFieldsMessage));
    }

    @Test
    @DisplayName("Создание пользователя без поля name")
    @Description("Проверка ошибки создания пользователя без поля name")
    public void createUserWithoutNameTest() {
        UserCreateRequest userCreateAndEditRequest = new UserCreateRequest(email, password, null);
        userSteps.userCreate(userCreateAndEditRequest)
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo(requiredFieldsMessage));
    }
}
