package uk.gov.cca.api.common.domain;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class HistoryPayload {

    @Size(max = 10000)
    private String comments;

    @Builder.Default
    private Map<UUID, String> evidenceFiles = new HashMap<>();
}
