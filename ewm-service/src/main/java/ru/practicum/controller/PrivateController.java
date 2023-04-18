package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.EventFullDto;
import ru.practicum.model.event.EventShortDto;
import ru.practicum.model.event.NewEventDto;
import ru.practicum.model.event.UpdateEventUserRequest;
import ru.practicum.model.request.ParticipationRequestDto;
import ru.practicum.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}")
public class PrivateController {
    private EventService eventService;
    private CategoryService categoryService;
    private UserService userService;
    private CompilationService compilationService;

    @GetMapping("/events")
    List<EventShortDto> getEvents(@PathVariable Long userId,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Get events by user " + userId);
        return eventService.getEventsByUser(userId, from, size);
    }

    @PatchMapping("/events")
    EventFullDto patchEvent(@PathVariable Long userId,
                            @RequestBody @Valid UpdateEventUserRequest updateEventRequest, HttpServletRequest request) {
        log.info("Update event by user " + userId);
        return eventService.modifyEvent(userId, updateEventRequest, request);
    }

    @PostMapping("/events")
    EventFullDto postEvent(@PathVariable Long userId,
                           @RequestBody @Valid NewEventDto newEventDto, HttpServletRequest request) {
        log.info("Create event by user " + userId);
        return eventService.addNewEvent(userId, newEventDto, request);
    }

    @GetMapping("/events/{eventId}")
    EventFullDto getEvent(@PathVariable Long userId,
                          @PathVariable Long eventId, HttpServletRequest request) {
        log.info("Get event " + eventId);
        return eventService.getEventByUser(userId, eventId, request);
    }

    @PatchMapping("/events/{eventId}")
    EventFullDto patchEventCancellation(@PathVariable Long userId,
                                        @PathVariable Long eventId, HttpServletRequest request) {
        log.info("update event " + eventId);
        return eventService.cancelEvent(userId, eventId, request);
    }

    @GetMapping("/events/{eventId}/requests")
    List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        log.info("get event by user " + userId);
        return eventService.getEventRequestsByUser(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    ParticipationRequestDto patchRequestConfirmation(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @PathVariable Long reqId, HttpServletRequest request) {
        log.info("confirm event by id " + reqId);
        return eventService.confirmEventRequest(userId, eventId, reqId, request);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    ParticipationRequestDto patchRequestRejection(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long reqId, HttpServletRequest request) {
        log.info("reject event by id " + reqId);
        return eventService.rejectEventRequest(userId, eventId, reqId, request);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") Long userId) {
        log.info("Get request by user " + userId);
        return eventService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto addNewRequest(@PathVariable("userId") Long userId,
                                                 @RequestParam Long eventId, HttpServletRequest request) {
        log.info("Create request by user " + userId);
        return eventService.addNewRequest(userId, eventId, request);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
                                                 @PathVariable("requestId") Long reqId, HttpServletRequest request) {
        log.info("Update request " + reqId);
        return eventService.cancelRequest(userId, reqId, request);
    }
}
