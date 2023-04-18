package ru.practicum.exception;

import javax.servlet.http.HttpServletRequest;

public class NotFoundUserException extends NotFoundException {
    public NotFoundUserException(Long userId, HttpServletRequest request) {
        super(String.format("Пользователь %d не найден. Request path = %s", userId, request.getRequestURI()));
    }
}
