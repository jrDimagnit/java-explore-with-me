package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.category.CategoryDto;
import ru.practicum.model.category.NewCategoryDto;
import ru.practicum.model.compilation.CompilationDto;
import ru.practicum.model.compilation.NewCompilationDto;
import ru.practicum.model.event.EventFullDto;
import ru.practicum.model.event.UpdateEventAdminRequest;
import ru.practicum.model.user.NewUserRequest;
import ru.practicum.model.user.User;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;
import ru.practicum.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/admin")
public class AdminController {
    private EventService eventService;
    private CategoryService categoryService;
    private UserService userService;
    private CompilationService compilationService;

    @GetMapping("/events")
    List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                 @RequestParam(required = false) List<String> states,
                                 @RequestParam(required = false) List<Long> categories,
                                 @RequestParam(required = false) String rangeStart,
                                 @RequestParam(required = false) String rangeEnd,
                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                 @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Request events");
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    EventFullDto putEvent(@PathVariable Long eventId,
                          @RequestBody @Valid UpdateEventAdminRequest adminUpdateEventRequest,
                          HttpServletRequest request) {
        log.info("Create event with id {},  event  {}", eventId, adminUpdateEventRequest);
        return eventService.modifyEventByAdmin(eventId, adminUpdateEventRequest, request);
    }

    @GetMapping("/users")
    List<User> getUsers(@RequestParam(required = false) List<Long> ids,
                        @RequestParam(required = false, defaultValue = "0") int from,
                        @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Get users");
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    User postUser(@RequestBody @Valid NewUserRequest newUserRequest, HttpServletRequest request) {
        log.info("Create user " + newUserRequest);
        return userService.addNewUser(newUserRequest, request);
    }

    @DeleteMapping("/users/{userId}")
    void deleteUser(@PathVariable Long userId) {
        log.info("Delete user " + userId);
        userService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    CompilationDto postCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto,
                                   HttpServletRequest request) {
        log.info("Post compilation");
        return compilationService.addNewCompilation(newCompilationDto, request);
    }

    @DeleteMapping("/compilations/{compId}")
    void deleteCompilation(@PathVariable Long compId) {
        log.info("Delete compilation " + compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    CompilationDto patchCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto, @PathVariable Long compId,
                                    HttpServletRequest request) {
        log.info("Patch compilation " + compId);
        return compilationService.patchCompilation(newCompilationDto, compId, request);
    }

    @PostMapping("/categories")
    CategoryDto postCategory(@RequestBody @Valid NewCategoryDto newCategoryDto, HttpServletRequest request) {
        log.info("Post category " + newCategoryDto);
        return categoryService.addNewCategory(newCategoryDto, request);
    }

    @PatchMapping("/categories/{catId}")
    CategoryDto patchCategory(@RequestBody @Valid CategoryDto categoryDto,
                              @PathVariable Long catId,
                              HttpServletRequest request) {
        log.info("Update category " + categoryDto);
        return categoryService.modifyCategory(categoryDto, request, catId);
    }

    @DeleteMapping("/categories/{catId}")
    void deleteCategory(@PathVariable Long catId) {
        log.info("Delete category " + catId);
        categoryService.deleteCategory(catId);
    }
}
