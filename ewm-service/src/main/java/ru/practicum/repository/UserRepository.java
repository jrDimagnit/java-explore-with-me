package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
