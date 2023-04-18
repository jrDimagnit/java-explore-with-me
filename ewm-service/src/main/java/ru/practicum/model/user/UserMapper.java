package ru.practicum.model.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDto toUserShortDto(User user) {
        return new UserDto(user.getId(), user.getName());
    }

    public User toUser(NewUserRequest newUser) {
        return new User(
                null,
                newUser.getName(),
                newUser.getEmail());
    }
}
