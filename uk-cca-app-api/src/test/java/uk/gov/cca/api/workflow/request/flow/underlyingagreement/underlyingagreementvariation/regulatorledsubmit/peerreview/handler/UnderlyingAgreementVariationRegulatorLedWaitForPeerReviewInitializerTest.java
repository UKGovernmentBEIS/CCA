package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedWaitForPeerReviewInitializerTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedWaitForPeerReviewInitializer initializer;

    @Test
    void initializePayload() {
        final AccountReferenceData accountReferenceData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder().build())
                .build();
        final UnderlyingAgreementVariationPayload underlyingAgreement = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder().status(FacilityStatus.LIVE).build()))
                        .build())
                .build();
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .build();
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of("facility", LocalDate.now());
        final Map<UUID, String> underlyingAgreementAttachments = Map.of(UUID.randomUUID(), "attachment");
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "reg-led-attachment");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "COMPLETED");
        final Request request = Request.builder()
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreementProposed(underlyingAgreement)
                        .regulatorLedDetermination(determination)
                        .regulatorLedFacilityChargeStartDateMap(facilityChargeStartDateMap)
                        .underlyingAgreementAttachments(underlyingAgreementAttachments)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .build();

        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload expected =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD)
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreement)
                        .determination(determination)
                        .facilityChargeStartDateMap(facilityChargeStartDateMap)
                        .underlyingAgreementAttachments(underlyingAgreementAttachments)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        final RequestTaskPayload result = initializer.initializePayload(request);

        // Verify
        assertThat(result)
                .isInstanceOf(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.class)
                .isEqualTo(expected);

    }
}
