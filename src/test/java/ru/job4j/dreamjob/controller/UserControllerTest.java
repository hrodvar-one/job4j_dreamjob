package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenGetRegistrationPageThenReturnRegistrationView() {
        var view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    void whenRegisterNewUserThenRedirectToVacancies() {
        var user = new User(1, "test@example.com", "Test User", "password");
        when(userService.save(any())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void whenRegisterExistingUserThenReturnErrorView() {
        when(userService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var user = new User(1, "test@example.com", "Test User", "password");
        var view = userController.register(model, user);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    void whenGetLoginPageThenReturnLoginView() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void whenLoginWithValidCredentialsThenRedirectToVacancies() {
        var user = new User(1, "test@example.com", "Test User", "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");
        verify(session).setAttribute("user", user);
    }

    @Test
    void whenLoginWithInvalidCredentialsThenReturnLoginViewWithError() {
        when(userService.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var user = new User(1, "test@example.com", "Test User", "wrongpassword");
        var view = userController.loginUser(user, model, mock(HttpServletRequest.class));
        var error = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    void whenLogoutThenInvalidateSessionAndRedirectToLogin() {
        var session = mock(HttpSession.class);

        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
        verify(session).invalidate();
    }
}