package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundCompilationException;
import ru.practicum.exception.NotFoundEventException;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.CompilationDto;
import ru.practicum.model.compilation.CompilationMapper;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.model.compilation.NewCompilationDto;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CompilationService {
    private CompilationRepository compilationRepository;
    private EventRepository eventRepository;
    private CompilationMapper compilationMapper;

    public List<CompilationDto> getCompilations(Boolean pinned, Integer fromNum, Integer size) {
        Integer from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Compilation> compilations;

        if (pinned == null)
            compilations = compilationRepository.findAll(page);
        else
            compilations = compilationRepository.findByPinned(pinned, page);

        if (compilations == null)
            compilations = Page.empty();

        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationDtoList.add(compilationMapper.toCompilationDto(compilation));
        }
        return compilationDtoList;
    }

    public CompilationDto getCompilation(Long compId, HttpServletRequest request) {
        Compilation compilation = checkCompilation(compId, request);
        return compilationMapper.toCompilationDto(compilation);
    }

    public Compilation checkCompilation(Long compId, HttpServletRequest request) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundCompilationException(compId, request));
    }

    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto, HttpServletRequest request) {
        if (newCompilationDto.getTitle() == null)
            throw new BadRequestException(request.getParameterMap().toString());
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        return compilationMapper.toCompilationDto(
                compilationRepository.save(compilationMapper.toCompilation(newCompilationDto, events)));
    }

    public CompilationDto patchCompilation(NewCompilationDto newCompilationDto,
                                           Long compId, HttpServletRequest request) {
        Compilation checkComp = checkCompilation(compId, request);
        if (newCompilationDto.getPinned() != null) {
            checkComp.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getTitle() != null) {
            checkComp.setTitle(newCompilationDto.getTitle());
        }
        if (newCompilationDto.getEvents() != null) {
            List<Event> listEvent = new ArrayList<>();
            for (Long id : newCompilationDto.getEvents()) {
                listEvent.add(eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundEventException(id, request)));
            }
            checkComp.setEvents(listEvent);
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(checkComp));
    }

    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

}
