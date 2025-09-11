package uk.gov.cca.api.subsistencefees.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO {

    private String submitter;

    private LocalDateTime submissionDate;

    private FacilityPaymentStatus paymentStatus;
}
