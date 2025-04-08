package uk.gov.cca.api.subsistencefees.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubsistenceFeesSearchCriteria {

	@Size(min = 3, max = 255)
	private String term;
		
	private FacilityPaymentStatus markFacilitiesStatus;
	
	@Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
}
