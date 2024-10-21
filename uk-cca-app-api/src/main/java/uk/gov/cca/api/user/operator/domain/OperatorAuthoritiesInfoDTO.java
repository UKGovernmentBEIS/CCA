package uk.gov.cca.api.user.operator.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorAuthoritiesInfoDTO {

    @Builder.Default
    private List<OperatorAuthorityInfoDTO> authorities = new ArrayList<>();
    private boolean editable;
}
