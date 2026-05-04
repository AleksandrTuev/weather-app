//package com.dev.util;
//
//import com.dev.exception.InvalidUsernameException;
//import com.dev.exception.PasswordMismatchException;
//
//import static com.dev.util.ProjectConstants.*;
//
//public class ValidateUtil {
//    //invalid name
//    //- имя содержит пробелы, цифры и спец.символы
//    //- превышена длина
//    //- имя имеется в БД
//
//    public static void validateLoginParameters(String name, String password) {
//        validateName(name);
//        validatePassword(password);
//    }
//
//    public static void validateSigUpParameters(String name, String password, String repeatPassword) {
//        validateName(name);
//        validatePassword(password);
//        checkMatchPasswords(password, repeatPassword);
//    }
//
//    private static void validateName(String name) {
//        checkNameOnNullAndEmpty(name);
//        checkNameValidity(name);
//        checkNameLength(name);
//    }
//
//    private static void validatePassword(String password) {
//        checkPasswordOnNullAndEmpty(password);
//        checkPasswordValidity(password);
//        checkPasswordLength(password);
//    }
//
//    private static void checkMatchPasswords(String password, String repeatPassword) {
//        if (!password.strip().equals(repeatPassword.strip())) {
//            throw new PasswordMismatchException("The password does not match");
//        }
//    }
//
//    private static void checkNameLength(String name) {
//        if ((name.length() < MIN_NAME_LENGTH) || (name.length() > MAX_NAME_LENGTH)) {
//            throw new InvalidUsernameException(
//                    String.format("The name length must be between %d to %d characters", MIN_NAME_LENGTH, MAX_NAME_LENGTH));
//        }
//    }
//
//    private static void checkPasswordLength(String password) {
//        if ((password.length() < MIN_PASSWORD_LENGTH) || (password.length() > MAX_PASSWORD_LENGTH)) {
//            throw new PasswordMismatchException(
//                    String.format("The password length must be between %d to %d characters",
//                            MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
//        }
//    }
//
//    private static void checkNameOnNullAndEmpty(String name) {
//        if (name.isBlank()) {
//            throw new InvalidUsernameException("The name must not be empty");
//        }
//    }
//
//    private static void checkPasswordOnNullAndEmpty(String password) {
//        if (password.isBlank()) {
//            throw new PasswordMismatchException("The password must not be empty");
//        }
//    }
//
//    private static void checkNameValidity(String name) {
//        String regex = "^\\p{L}+$";
//        if (!name.matches(regex)) {
//            throw new InvalidUsernameException("Invalid name");
//        }
//    }
//
//    private static void checkPasswordValidity(String password) {
//        String regex = "^\\S+$";
//        if (!password.matches(regex)) {
//            throw new PasswordMismatchException("Invalid password");
//        }
//    }
//}
