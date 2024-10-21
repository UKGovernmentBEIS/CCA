package uk.gov.cca.api.authorization.ccaauth.operator.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CcaOperatorAuthorityDeletionEvent {

    private String userId;
    private Long accountId;
    private boolean existAuthoritiesOnOtherAccounts;
}
