package ru.practicum.model.request;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class RequestMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(formatter),
                request.getEvent(),
                request.getRequester(),
                request.getStatus());
    }
}
