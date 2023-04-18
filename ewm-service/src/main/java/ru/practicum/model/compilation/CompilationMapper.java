package ru.practicum.model.compilation;

import org.springframework.stereotype.Service;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationMapper {
    private EventMapper eventMapper;

    public Compilation toCompilation(CompilationDto dto, List<Event> events) {
        return new Compilation(
                dto.getId(),
                dto.getPinned(),
                dto.getTitle(),
                events);
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents().stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()),
                compilation.getPinned(),
                compilation.getTitle());
    }

    public Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        return new Compilation(
                null,
                dto.getPinned(),
                dto.getTitle(),
                events);
    }
}
