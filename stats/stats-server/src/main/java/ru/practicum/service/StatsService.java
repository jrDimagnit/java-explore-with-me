package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.model.HitMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StatsService {
    private StatsRepository statsRepository;
    private HitMapper hitMapper;

    public void addHit(EndpointHit endpointHit) {
        log.debug("add Hit {}", endpointHit);
        statsRepository.addHit(hitMapper.map(endpointHit));
    }

    public Collection<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        return statsRepository.getStats(start, end, Arrays.stream(uris).collect(Collectors.toList()), unique);
    }
}
