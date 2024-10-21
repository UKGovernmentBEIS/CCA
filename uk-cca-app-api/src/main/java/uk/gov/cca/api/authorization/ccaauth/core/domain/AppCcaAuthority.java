package uk.gov.cca.api.authorization.ccaauth.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AppCcaAuthority extends AppAuthority {

    private Long sectorAssociationId;
}
