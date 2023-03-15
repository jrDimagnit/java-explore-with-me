package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@AllArgsConstructor
@Slf4j
public class StatsServerController {
    private StatsService statsService;

    @PostMapping(path = "/hit")
    public void addHit(@RequestBody @Valid EndpointHit endpointHit) {
        log.debug("Get endpoint {}", endpointHit);
        endpointHit.setIp("127.0.0.1");
        statsService.addHit(endpointHit);
    }

    @GetMapping(path = "/stats")
    public Collection<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(required = false) String[] uris,
                                          @RequestParam(required = false,defaultValue = "false") Boolean unique) {
        log.debug("Get stats with params");
        return statsService.getStats(start, end, uris, unique);
    }
}
