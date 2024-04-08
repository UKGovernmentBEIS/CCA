package uk.gov.cca.api.authorization.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

/**
 * The authenticated User's applicable accounts.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppAuthority {

    private String code;

    private Long accountId;

    private CompetentAuthorityEnum competentAuthority;

    private Long verificationBodyId;

    private List<String> permissions;
}
