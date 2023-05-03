package ru.practicum.model.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.UserDto;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    String annotation;
    Category category;
    Long confirmedRequests;
    String eventDate;
    UserDto initiator;
    Boolean paid;
    String title;
    Long views;
}
