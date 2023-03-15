package ru.practicum.model;

import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;

@Service
public class HitMapper {
    public Hit map(EndpointHit endpointHit) {
        return new Hit(null, endpointHit.getApp(), endpointHit.getUri(),
                endpointHit.getIp(), endpointHit.getTimestamp());
    }
}
