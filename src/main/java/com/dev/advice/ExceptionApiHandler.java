package com.dev.advice;

import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
import com.dev.exception.InvalidPasswordException;
import com.dev.exception.UserNotFoundException;
import com.dev.exception.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import static com.dev.util.ProjectConstants.*;

@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionApiHandler {
    public static final String SIGN_IN_WITH_ERRORS = "sign-in-with-errors";
    private static final String SIGN_UP_WITH_ERRORS = "sign-up-with-errors";

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ModelAndView handleAlreadyExistsName(HttpServletRequest req, UsernameAlreadyExistsException e) {
        log.error(e.getMessage(), e);
        return createModelAndView(req, SIGN_UP_WITH_ERRORS, e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPasswordException.class)
    public ModelAndView handleInvalidNameOrPassword(HttpServletRequest req, InvalidPasswordException e) {
        log.error(e.getMessage(), e);
        return createModelAndView(req, SIGN_IN_WITH_ERRORS, e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleNotFound(HttpServletRequest req, UserNotFoundException e) {
        log.error(e.getMessage(), e);
        return createModelAndView(req, SIGN_IN_WITH_ERRORS, e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e) {
        log.error(e.getMessage(), e);
        return "error";
    }

    private ModelAndView createModelAndView(HttpServletRequest req, String viewName, Exception e) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        switch (viewName) {
            case SIGN_IN_WITH_ERRORS: {
                modelAndView.addObject(USER, getUserSignInDto(req));
                break;
            }
            case SIGN_UP_WITH_ERRORS: {
                modelAndView.addObject(USER, getUserSignUpDto(req));
                break;
            }
        }
        modelAndView.addObject(MESSAGE, e.getMessage());
        return modelAndView;
    }

    private UserSignInDto getUserSignInDto(HttpServletRequest req) {
        UserSignInDto userSignInDto = new UserSignInDto();
        userSignInDto.setUsername(req.getParameter(USERNAME));
        return userSignInDto;
    }

    private UserSignUpDto getUserSignUpDto(HttpServletRequest req) {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setUsername(req.getParameter(USERNAME));
        return userSignUpDto;
    }
}
