package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeOfIntent {

    @NotNull
    private UUID noticeOfIntentFile;

    @Size(max = 10000)
    private String comments;
}
