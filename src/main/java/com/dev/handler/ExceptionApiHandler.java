package com.dev.handler;

import com.dev.controller.SignInController;
import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
import com.dev.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import static com.dev.util.ProjectConstants.*;

@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionApiHandler {
    private final HttpServletRequest req;

    public static final String SIGN_IN_WITH_ERRORS = "sign-in-with-errors";
    private static final String SIGN_UP_WITH_ERRORS = "sign-up-with-errors";

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ModelAndView handleAlreadyExistsName(UsernameAlreadyExistsException e) {
        ModelAndView modelAndView = new ModelAndView(SIGN_UP_WITH_ERRORS);
        log.error(e.getMessage());

        modelAndView.addObject(USER, getUserSignInDto());
        modelAndView.addObject(MESSAGE, e.getMessage());

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidPasswordException.class, PasswordMismatchException.class, InvalidUsernameException.class})
    public ModelAndView handleInvalidNameOrPassword(Exception e, HandlerMethod handlerMethod) {
        ModelAndView modelAndView;
        log.error(e.getMessage());

        if (handlerMethod.getBeanType().equals(SignInController.class)) {
            modelAndView = new ModelAndView(SIGN_IN_WITH_ERRORS);
            modelAndView.addObject(USER, getUserSignInDto());
            modelAndView.addObject(MESSAGE, e.getMessage());
        } else {
            modelAndView = new ModelAndView(SIGN_UP_WITH_ERRORS);
            modelAndView.addObject(USER, getUserSignUpDto());
            modelAndView.addObject(MESSAGE, e.getMessage());
        }

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleNotFound(UserNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView(SIGN_IN_WITH_ERRORS);
        log.error(e.getMessage());

        modelAndView.addObject(USER, getUserSignInDto());
        modelAndView.addObject(MESSAGE, e.getMessage());

        return modelAndView;
    }

    private UserSignInDto getUserSignInDto() {
        UserSignInDto userSignInDto = new UserSignInDto();
        userSignInDto.setUsername(req.getParameter(USERNAME));
        userSignInDto.setPassword(req.getParameter(PASSWORD));
        return userSignInDto;
    }

    private UserSignUpDto getUserSignUpDto() {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setUsername(req.getParameter(USERNAME));
        userSignUpDto.setPassword(req.getParameter(PASSWORD));
        userSignUpDto.setRepeatPassword(req.getParameter(REPEAT_PASSWORD));
        return userSignUpDto;
    }
}
