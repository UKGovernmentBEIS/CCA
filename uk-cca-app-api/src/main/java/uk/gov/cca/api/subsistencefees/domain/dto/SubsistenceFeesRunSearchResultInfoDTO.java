package uk.gov.cca.api.subsistencefees.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesRunSearchResultInfoDTO {

	private Long runId;

	private String paymentRequestId;

    private LocalDateTime submissionDate;

    private PaymentStatus paymentStatus;

    private FacilityPaymentStatus markFacilitiesStatus;

    private BigDecimal currentTotalAmount;

    private BigDecimal outstandingTotalAmount;
}
