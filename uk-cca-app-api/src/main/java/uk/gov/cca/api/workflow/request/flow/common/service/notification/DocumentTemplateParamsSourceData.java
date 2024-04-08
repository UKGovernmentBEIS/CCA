package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplateParamsSourceData {
    
    private DocumentTemplateGenerationContextActionType contextActionType;
    private Request request;
    private String signatory;
    private UserInfoDTO accountPrimaryContact;
    
    private String toRecipientEmail;
    @Builder.Default
    private List<String> ccRecipientsEmails = new ArrayList<>();
}
