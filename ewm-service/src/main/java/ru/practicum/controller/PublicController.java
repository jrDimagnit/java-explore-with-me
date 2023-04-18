package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.category.CategoryDto;
import ru.practicum.model.compilation.CompilationDto;
import ru.practicum.model.event.EventFullDto;
import ru.practicum.model.event.EventShortDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
public class PublicController {
    private EventService eventService;
    private CompilationService compilationService;
    private CategoryService categoryService;

    @GetMapping("/events")
    List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                  @RequestParam(required = false) Long[] categories,
                                  @RequestParam(required = false) Boolean paid,
                                  @RequestParam(required = false) String rangeStart,
                                  @RequestParam(required = false) String rangeEnd,
                                  @RequestParam(required = false) Boolean onlyAvailable,
                                  @RequestParam(required = false) String sort,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size,
                                  HttpServletRequest request) {
        log.info("Get events by public");
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{eventId}")
    EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Get public event  " + eventId);
        return eventService.getEvent(eventId, request);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size,
                                                HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable("compId") Long compId, HttpServletRequest request) {
        log.info("Get public compilation " + compId);
        return compilationService.getCompilation(compId, request);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get public Categories");
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable("catId") Long catId, HttpServletRequest request) {
        log.info("Get public categiry " + catId);
        return categoryService.getCategory(catId, request);
    }
}
