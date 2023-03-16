package ru.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStats;
import ru.practicum.model.Hit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class StatsRepository {
    private final JdbcTemplate jdbcTemplate;

    public StatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addHit(Hit hit) {
        SimpleJdbcInsert simpleJdbcInsertFilm = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("stat")
                .usingGeneratedKeyColumns("id");
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("app", hit.getApp());
        parameters.put("uri", hit.getUri());
        parameters.put("ip", hit.getIp());
        parameters.put("timestamp", hit.getTimestamp());
        simpleJdbcInsertFilm.executeAndReturnKey(parameters).longValue();
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String sql;
        List<ViewStats> result;
        List<Object> params = new ArrayList<>();

        if (uris == null) {
            if (unique) {
                sql = "SELECT app, uri, count(distinct ip) as hits FROM stat " +
                        "WHERE timestamp BETWEEN ? AND ? GROUP BY app, uri ORDER BY cnt DESC";
            } else {
                sql = "SELECT app, uri, count(1) as hits FROM stat WHERE timestamp " +
                        "BETWEEN ? AND ? GROUP BY app, uri ORDER BY cnt DESC";
            }
            result = jdbcTemplate.query(sql, this::makeViewStat, Timestamp.valueOf(start), Timestamp.valueOf(end));
        } else {
            String urisList = String.join(",", Collections.nCopies(uris.size(), "?"));
            if (unique) {
                sql = String.format("SELECT app, uri, count(distinct ip) hits FROM stat " +
                        "WHERE timestamp BETWEEN ? AND ? AND uri IN (%s) GROUP BY app, uri ORDER BY hits DESC", urisList);
            } else {
                sql = String.format("SELECT app, uri, count(1) hits FROM stat " +
                        "WHERE timestamp BETWEEN ? AND ? AND uri IN (%s) GROUP BY app, uri ORDER BY hits DESC", urisList);
            }
            params.add(start);
            params.add(end);
            params.addAll(uris);
            result = jdbcTemplate.query(sql, this::makeViewStat, params.toArray());
        }

        return result;
    }

    private ViewStats makeViewStat(ResultSet rs, int rowNum) throws SQLException {
        return new ViewStats(rs.getInt("hits"),
                rs.getString("uri"), rs.getString("app"));

    }
}

