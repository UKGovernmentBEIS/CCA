package uk.gov.cca.api.web.controller.authorization.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStateDTO {

    private String userId;

    private String roleType;

    private LoginStatus status;
}
