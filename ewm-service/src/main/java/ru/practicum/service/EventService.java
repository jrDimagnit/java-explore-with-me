package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.StatsClient;
import ru.practicum.exception.*;
import ru.practicum.model.Location;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.*;
import ru.practicum.model.request.*;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class EventService {
    private EventRepository eventRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private RequestRepository requestRepository;
    private StatsClient statClient;
    private EventMapper eventMapper;
    private RequestMapper requestMapper;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, String sort,
                                         Integer fromNum, Integer size, HttpServletRequest request) {
        Pageable page;
        int from = fromNum >= 0 ? fromNum / size : 0;
        Page<Event> events;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusYears(100);
        statClient.hit(new EndpointHit("ewm-main", request.getRequestURI(), request.getRemoteAddr(), start));
        if (sort == null || sort.equals("EVENT_DATE"))
            page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        else if (sort.equals("VIEWS"))
            page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "views"));
        else
            throw new BadRequestException(request.getParameterMap().toString());
        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        events = eventRepository.getEventsCustom(text, categories, paid, start, end, onlyAvailable, page);
        if (events == null)
            events = Page.empty();
        return events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId, request);
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        statClient.hit(new EndpointHit("ewm-main", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        return eventMapper.toEventFullDto(event);
    }

    public Event checkEvent(Long eventId, HttpServletRequest request) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundEventException(eventId, request));
    }

    public List<EventShortDto> getEventsByUser(Long userId, Integer fromNum, Integer size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        List<Event> shortEvents = eventRepository.findByUser(userId, page);
        return shortEvents.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public EventFullDto getEventByUser(Long userId, Long eventId, HttpServletRequest request) {
        return eventMapper.toEventFullDto(checkEventByUser(userId, eventId, request));
    }

    private Event checkEventByUser(Long userId, Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId, request);
        if (!event.getInitiator().getId().equals(userId))
            throw new BadRequestException(request.getParameterMap().toString());
        else
            return event;
    }

    public EventFullDto addNewEvent(Long userId, NewEventDto eventDto, HttpServletRequest request) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), formatter);
        if (eventDto.getAnnotation() == null || eventDate.isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .plusHours(2)))
            throw new BadRequestException(request.getParameterMap().toString());
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundCategoryException(eventDto.getCategory(), request));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(userId, request));
        return eventMapper.toEventFullDto(eventRepository.save(eventMapper.toEvent(eventDto, user, category)));
    }

    public EventFullDto modifyEvent(Long userId, UpdateEventUserRequest eventDto, HttpServletRequest req) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), formatter);
        long eventId = eventDto.getEventId();
        Event event = checkEventByUser(userId, eventId, req);
        if (event.getState() == EventState.PUBLISHED || eventDate.isBefore(LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES).plusHours(2)))
            throw new BadRequestException(req.getParameterMap().toString());
        if (event.getState().toString().equals("CANCELED"))
            event.setState(EventState.PENDING);
        event = checkUpdates(event, eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                eventDto.getEventDate(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getTitle(), req);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public EventFullDto cancelEvent(Long userId, Long eventId, HttpServletRequest request) {
        Event event = checkEventByUser(userId, eventId, request);
        event.setState(EventState.CANCELED);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, Integer fromNum, Integer size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime start;
        LocalDateTime end;
        List<EventState> eStates = new ArrayList<>();
        if (states == null) {
            eStates = Arrays.asList(EventState.values());
        } else {
            for (String state : states) {
                eStates.add(EventState.valueOf(state));
            }
        }
        if (rangeStart == null)
            start = LocalDateTime.now().minusYears(100);
        else
            start = LocalDateTime.parse(rangeStart, formatter);
        if (rangeEnd == null)
            end = LocalDateTime.now().plusYears(100);
        else
            end = LocalDateTime.parse(rangeEnd, formatter);
        Page<Event> events = eventRepository.getEventsByAdmin(users, eStates, categories, start, end, page);
        return events.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public EventFullDto modifyEventByAdmin(Long eventId, UpdateEventAdminRequest eventDto, HttpServletRequest req) {
        Event event = checkEvent(eventId, req);
        Location location = eventDto.getLocation();

        event = checkUpdates(event, eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                eventDto.getEventDate(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getTitle(), req);
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }
        if (eventDto.getRequestModeration() != null)
            event.setRequestModeration(eventDto.getRequestModeration());

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Event checkUpdates(Event event, String annotation, String description, Long catId, String eventDate,
                               Boolean paid, Long participantLimit, String title, HttpServletRequest request) {
        if (annotation != null)
            event.setAnnotation(annotation);
        if (description != null)
            event.setDescription(description);
        if (catId != null) {
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundCategoryException(catId, request));
            event.setCategory(category);
        }
        if (eventDate != null)
            event.setEventDate(LocalDateTime.parse(eventDate, formatter));
        if (paid != null)
            event.setPaid(paid);
        if (participantLimit != null)
            event.setParticipantLimit(participantLimit);
        if (title != null)
            event.setTitle(title);
        return event;
    }

    public ParticipationRequestDto confirmEventRequest(Long userId, Long eventId, Long reqId,
                                                       HttpServletRequest httpReq) {
        Event event = checkEventByUser(userId, eventId, httpReq);
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundRequestException(reqId, httpReq));
        long limit = event.getParticipantLimit();
        long confirmedRequests = event.getConfirmedRequests();
        if (limit == 0 || !event.getRequestModeration() || limit == confirmedRequests)
            throw new BadRequestException(httpReq.getParameterMap().toString());

        if (confirmedRequests < event.getParticipantLimit()) {
            event.setConfirmedRequests(confirmedRequests + 1);
            request.setStatus("CONFIRMED");
            eventRepository.save(event);
            requestRepository.save(request);
        } else
            throw new BadRequestException(httpReq.getParameterMap().toString());

        if (limit == event.getConfirmedRequests()) {
            List<Request> reqs = requestRepository.findByEvent(eventId);
            reqs.stream().filter(req -> req.getStatus().equals("PENDING")).forEach((req) -> {
                req.setStatus("REJECTED");
                requestRepository.save(req);
            });
        }
        return requestMapper.toParticipationRequestDto(request);
    }

    public ParticipationRequestDto rejectEventRequest(Long userId, Long eventId, Long reqId,
                                                      HttpServletRequest httpReq) {
        Event event = checkEventByUser(userId, eventId, httpReq);
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundRequestException(reqId, httpReq));
        long num = event.getConfirmedRequests() - 1;
        event.setConfirmedRequests(num >= 0 ? num : 0);
        request.setStatus("REJECTED");
        eventRepository.save(event);
        requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(request);
    }

    public EventRequestsByStatusResponseDto updateEventParticipationRequestStatus(
            Long userId, Long eventId, UpdateEventParticipationStatusRequestDto requestStatusDto,
            HttpServletRequest httpReq) {
        EventRequestsByStatusResponseDto eventRequestsByStatusResponseDto = new EventRequestsByStatusResponseDto();
        List<ParticipationRequestDto> responseRequest = new ArrayList<>();
        if (Objects.equals(requestStatusDto.getStatus(), "CONFIRMED")) {
            for (Long id : requestStatusDto.getRequestIds()) {
                responseRequest.add(confirmEventRequest(userId, eventId, id, httpReq));
            }
            eventRequestsByStatusResponseDto.setConfirmedRequests(responseRequest);
        }
        if (Objects.equals(requestStatusDto.getStatus(), "REJECTED")) {
            for (Long id : requestStatusDto.getRequestIds()) {
                responseRequest.add(rejectEventRequest(userId, eventId, id, httpReq));
            }
            eventRequestsByStatusResponseDto.setRejectedRequests(responseRequest);
        }
        return eventRequestsByStatusResponseDto;
    }


    public ParticipationRequestDto cancelRequest(Long userId, Long reqId, HttpServletRequest httpReq) {
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundRequestException(reqId, httpReq));
        Event event = checkEvent(request.getEvent(), httpReq);
        if (request.getRequester().equals(userId)) {
            request.setStatus("CANCELED");
            long num = event.getConfirmedRequests() - 1;
            event.setConfirmedRequests(num >= 0 ? num : 0);
        } else
            throw new BadRequestException(httpReq.getParameterMap().toString());

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getEventRequestsByUser(Long userId, Long eventId, HttpServletRequest request) {
        Event event = checkEvent(userId, request);
        if (event.getId().equals(eventId)) {
            List<Request> requests = requestRepository.findByEvent(eventId);
            return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
        } else {
            throw new NotFoundEventException(eventId, request);
        }
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        List<Request> requests = requestRepository.findByRequester(userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public ParticipationRequestDto addNewRequest(Long userId, Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundEventException(eventId, request));
        List<Request> requests = requestRepository.findByRequester(userId);
        List<Long> reqIds = requests.stream().map(Request::getId).collect(Collectors.toList());
        if (!event.getState().equals(EventState.PUBLISHED) || event.getInitiator().getId().equals(userId)
                || event.getConfirmedRequests().equals(event.getParticipantLimit()) || reqIds.contains(userId))
            throw new BadRequestException(request.getParameterMap().toString());
        Request newRequest = new Request(null, LocalDateTime.now(), eventId, userId, "PENDING");
        if (!event.getRequestModeration())
            newRequest.setStatus("CONFIRMED");
        return requestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }
}
