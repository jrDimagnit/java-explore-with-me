package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class StatsClient {
    private final String application;
    private final WebClient client;
    private final String statsServiceUri;


    public StatsClient(@Value("${spring.application.name}") String application,
                       @Value("${service.stats-service.uri}") String statsServiceUri) {
        this.application = application;
        this.statsServiceUri = statsServiceUri;
        this.client = WebClient.create(statsServiceUri);
    }

    public void hit(String ip, String uri) {
        EndpointHit requestHit = new EndpointHit();
        requestHit.setApp(application);
        requestHit.setIp(ip);
        requestHit.setUri(uri);
        requestHit.setTimestamp(LocalDateTime.now());
        client.post()
                .uri("/hit")
                .bodyValue(requestHit)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public Collection<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder urisRequest = new StringBuilder();
        for (String u : uris) {
            urisRequest.append(u);
            urisRequest.append("&");
        }
        String uri = statsServiceUri + "/stats" + "?start=" + start.toString() + "&end=" + end.toString() +
                "&" + urisRequest + "&" + unique.toString();
        return client.get()
                .uri("/stats" + uri)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .retrieve()
                .bodyToFlux(ViewStats.class)
                .collectList()
                .block();
    }
}