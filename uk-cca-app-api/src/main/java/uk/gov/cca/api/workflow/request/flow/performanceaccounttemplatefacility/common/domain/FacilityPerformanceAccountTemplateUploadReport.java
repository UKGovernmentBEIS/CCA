package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;

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

    @Builder.Default
    private List<FacilityPerformanceAccountTemplateSavingAction> savingActions = new ArrayList<>();

    private boolean succeeded;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
