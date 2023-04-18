package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.user.NewUserRequest;
import ru.practicum.model.user.User;
import ru.practicum.model.user.UserMapper;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;

    public List<User> getUsers(Long[] users, Integer fromNum, Integer size) {
        if (users != null) {
            return userRepository.findAllById(Arrays.asList(users));
        } else {
            Integer from = fromNum >= 0 ? fromNum / size : 0;
            Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
            Page<User> userPage = userRepository.findAll(page);

            return userPage.stream().collect(Collectors.toList());
        }
    }

    public User addNewUser(NewUserRequest newUser, HttpServletRequest request) {
        if (newUser.getName() == null)
            throw new BadRequestException(request.getParameterMap().toString());
        Set<String> emails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toSet());
        if (emails.contains(newUser.getEmail()))
            throw new ConflictException("Повторяющееся значение email = " + newUser.getEmail() +
                    ". Request path = " + request.getRequestURI());
        else
            return userRepository.save(userMapper.toUser(newUser));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
