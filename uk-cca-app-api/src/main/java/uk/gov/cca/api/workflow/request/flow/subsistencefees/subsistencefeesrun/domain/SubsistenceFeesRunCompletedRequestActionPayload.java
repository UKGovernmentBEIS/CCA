package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain;

import java.time.Year;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubsistenceFeesRunCompletedRequestActionPayload extends CcaRequestActionPayload {

	@NotNull
	private String paymentRequestId;
	
	@NotNull
	private Year chargingYear;
	
	@NotNull
	private String status;
	
	@NotNull
	@PositiveOrZero
	private Long sentInvoices;
	
	@PositiveOrZero
	private Long failedInvoices;
	
    private FileInfoDTO report;

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(
                super.getFileDocuments(),
                Map.of(UUID.fromString(report.getUuid()), report.getName())
        ).flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
