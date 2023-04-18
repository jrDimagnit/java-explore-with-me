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
    @Column(name = "confirmedRequests")
    Long confirmedRequests;
    @Column(name = "created")
    LocalDateTime createdOn;
    @Column(name = "eventDate", nullable = false)
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator", nullable = false)
    User initiator;
    @Column(nullable = false)
    Float lat;
    @Column(nullable = false)
    Float lon;
    @Column(nullable = false)
    Boolean paid;
    @Column(name = "participantLimit")
    Long participantLimit;
    @Column(name = "publishedOn", nullable = false)
    LocalDateTime publishedOn;
    @Column(name = "requestModeration")
    Boolean requestModeration = true;
    @Enumerated(EnumType.STRING)
    EventState state;
    @Column(nullable = false)
    String title;
    @Column
    Long views;

}
