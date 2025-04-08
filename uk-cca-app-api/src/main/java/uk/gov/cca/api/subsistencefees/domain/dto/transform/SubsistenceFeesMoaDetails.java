package uk.gov.cca.api.subsistencefees.domain.dto.transform;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.subsistencefees.domain.MoaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesMoaDetails {

	private Long moaId;

	private String transactionId;

	private MoaType moaType;

	private Long resourceId;

    private BigDecimal initialTotalAmount;

    private BigDecimal receivedAmount;

    private String documentUuid;

    private LocalDateTime submissionDate;

    private BigDecimal facilityFee;

    private BigDecimal currentTotalAmount;

    private Long totalFacilities;

    private Long paidFacilities;

    private Long moaTargetUnitId;
}
