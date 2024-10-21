package uk.gov.cca.api.workflow.request.flow.common.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultNoticeRecipient {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private NoticeRecipientType recipientType;
}
