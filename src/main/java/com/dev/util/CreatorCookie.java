package com.dev.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CreatorCookie {
    public static void create(String paramName, String paramValue, HttpServletResponse res) {
        Cookie cookie = new Cookie(paramName, String.valueOf(paramValue));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        cookie.setHttpOnly(true);
        res.addCookie(cookie);
    }
}
