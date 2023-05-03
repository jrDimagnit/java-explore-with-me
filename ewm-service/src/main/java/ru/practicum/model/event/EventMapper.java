package ru.practicum.model.event;

import org.springframework.stereotype.Service;
import ru.practicum.model.Location;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.CategoryMapper;
import ru.practicum.model.user.User;
import ru.practicum.model.user.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventMapper {
    private final CategoryMapper categoryMapper = new CategoryMapper();
    private final UserMapper userMapper = new UserMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                categoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(formatter),
                userMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews());
    }

    public EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                event.getDescription(),
                categoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(formatter),
                event.getEventDate().format(formatter),
                userMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLat(), event.getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(formatter),
                event.getRequestModeration(),
                event.getState().toString(),
                event.getTitle(),
                event.getViews());
    }

    public Event toEvent(NewEventDto newEvent, User user, Category category) {
        return new Event(
                null,
                newEvent.getAnnotation(),
                newEvent.getDescription(),
                category,
                0L,
                LocalDateTime.now(),
                LocalDateTime.parse(newEvent.getEventDate(), formatter),
                user,
                newEvent.getLocation().getLat(),
                newEvent.getLocation().getLon(),
                newEvent.getPaid(),
                newEvent.getParticipantLimit(),
                LocalDateTime.now(),
                newEvent.getRequestModeration(),
                EventState.PENDING,
                newEvent.getTitle(),
                0L);
    }
}
