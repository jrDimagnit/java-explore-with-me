package ru.practicum;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStats {
    String app;
    String uri;
    Integer hits;

    public ViewStats(Integer hits, String app, String uri) {
        this.hits = hits;
        this.app = app;
        this.uri = uri;
    }
}
