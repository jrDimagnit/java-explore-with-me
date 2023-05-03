package ru.practicum.model.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.user.User;
import ru.practicum.model.category.Category;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String annotation;
    @Column
    String description;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
    @Column(name = "confirmed_requests")
    Long confirmedRequests;
    @Column(name = "created")
    LocalDateTime createdOn;
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User initiator;
    @Column(nullable = false)
    Float lat;
    @Column(nullable = false)
    Float lon;
    @Column(nullable = false)
    Boolean paid;
    @Column(name = "participation_limit")
    Long participantLimit;
    @Column(name = "published_date", nullable = false)
    LocalDateTime publishedOn;
    @Column(name = "moderation")
    Boolean requestModeration = true;
    @Enumerated(EnumType.STRING)
    EventState state;
    @Column(nullable = false)
    String title;
    @Column
    Long views;

}
