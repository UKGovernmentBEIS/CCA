package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#manufacturingProcessFile != null || #processFlowMapsFile != null || #annotatedSitePlansFile != null || #eligibleProcessFile != null || #directlyAssociatedActivitiesFile != null || #seventyPerCentRuleEvidenceFile != null || #baseYearTargetPeriodEvidenceFiles.size() > 0 || #additionalDocuments.size() > 0}",
        message = "facilityAudit.preAuditReview.requestedDocuments")
public class RequestedDocuments {

    @NotNull
    @PastOrPresent
    private LocalDate auditMaterialReceivedDate;

    private UUID manufacturingProcessFile;

    private UUID processFlowMapsFile;

    private UUID annotatedSitePlansFile;

    private UUID eligibleProcessFile;

    private UUID directlyAssociatedActivitiesFile;

    private UUID seventyPerCentRuleEvidenceFile;

    @Builder.Default
    private Set<UUID> baseYearTargetPeriodEvidenceFiles = new HashSet<>();

    @Builder.Default
    private Set<UUID> additionalDocuments = new HashSet<>();

    @Size(max = 10000)
    private String additionalInformation;
}
