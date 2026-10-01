package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityAccountState implements Serializable {
	
	@Serial
    private static final long serialVersionUID = 1L;

    private Long accountId;

    private String businessId;

    private boolean succeeded;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
