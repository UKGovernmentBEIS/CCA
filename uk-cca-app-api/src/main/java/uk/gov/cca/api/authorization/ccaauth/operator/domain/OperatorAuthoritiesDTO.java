package uk.gov.cca.api.authorization.ccaauth.operator.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OperatorAuthoritiesDTO {

    private List<OperatorAuthorityDTO> authorities;
    private boolean editable;
}
