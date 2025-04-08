package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilitiesTemplateData {

    String groupId;
    String id;
    String name;
    String period;
    BigDecimal amount;
}
