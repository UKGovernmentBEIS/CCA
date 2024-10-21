package uk.gov.cca.api.web.orchestrator.authorization.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.web.orchestrator.authorization.validate.AccountOperatorAuthorityUpdate;
import uk.gov.netz.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;

import java.util.List;

@AccountOperatorAuthorityUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOperatorAuthorityUpdateWrapperDTO {

    @NotNull
    @Valid
    private List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdateList;
}
