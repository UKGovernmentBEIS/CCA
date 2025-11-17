package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload extends CcaRequestPayload {

    private String defaultSignatory;

    private AccountReferenceData accountReferenceData;

    private UnderlyingAgreement underlyingAgreement;

    @Builder.Default
    private List<Cca3FacilityMigrationData> facilityMigrationDataList = new ArrayList<>();

    @Builder.Default
    private Map<UUID, String> underlyingAgreementAttachments = new HashMap<>();

    private FileInfoDTO officialNotice;

    private FileInfoDTO underlyingAgreementDocument;

    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails activationDetails;

    private CcaDecisionNotification decisionNotification;

    @Builder.Default
    private Map<UUID, String> activationAttachments = new HashMap<>();
}
