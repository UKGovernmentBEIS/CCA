package uk.gov.cca.api.web.orchestrator.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.enumeration.AccountContactType;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountOperatorsUsersAuthoritiesInfoDTO extends UsersAuthoritiesInfoDTO {

    private Map<AccountContactType, String> contactTypes;
}
