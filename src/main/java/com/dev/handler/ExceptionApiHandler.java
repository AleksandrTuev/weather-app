package com.dev.handler;

import com.dev.dto.UserSignInDto;
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
    private final HttpServletRequest req;

    public static final String SIGN_IN_WITH_ERRORS = "sign-in-with-errors";
    private static final String SIGN_UP_WITH_ERRORS = "sign-up-with-errors";

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ModelAndView handleAlreadyExistsName(UsernameAlreadyExistsException e) {
        log.error(e.getMessage());
        return createModelAndView(SIGN_UP_WITH_ERRORS, e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPasswordException.class)
    public ModelAndView handleInvalidNameOrPassword(InvalidPasswordException e) {
        log.error(e.getMessage());
        return createModelAndView(SIGN_IN_WITH_ERRORS, e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleNotFound(UserNotFoundException e) {
        log.error(e.getMessage());
        return createModelAndView(SIGN_IN_WITH_ERRORS, e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e) {
        log.error(e.getMessage());
        return "error";
    }

    private ModelAndView createModelAndView(String viewName, Exception e) {
        ModelAndView modelAndView = new ModelAndView(viewName);
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
}
