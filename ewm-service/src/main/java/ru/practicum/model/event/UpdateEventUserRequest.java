package ru.practicum.model.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @NotNull
    Long eventId;
    String annotation;
    String description;
    Long category;
    String eventDate;
    Boolean paid;
    Integer participantLimit;
    String title;
}
