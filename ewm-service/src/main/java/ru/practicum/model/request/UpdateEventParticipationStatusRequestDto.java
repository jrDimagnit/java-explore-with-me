package ru.practicum.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventParticipationStatusRequestDto {
    @NotNull
    private Set<Long> requestIds;
    @NotNull
    private String status;
}
