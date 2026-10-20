package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceAccountTemplateUploadReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long facilityId;

    private String facilityBusinessId;

    private Long accountId;

    private boolean succeeded;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
