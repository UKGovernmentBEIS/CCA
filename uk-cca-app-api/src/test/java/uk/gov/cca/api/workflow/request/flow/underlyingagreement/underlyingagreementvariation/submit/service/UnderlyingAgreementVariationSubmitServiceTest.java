package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.transform.UnderlyingAgreementVariationSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.validation.UnderlyingAgreementVariationPayloadValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationSubmitServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationSubmitService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementVariationPayloadValidatorService underlyingAgreementVariationPayloadValidatorService;

    @Test
    void submitUnderlyingAgreementVariation() {
        final AppUser authUser = AppUser.builder().userId("user1").build();
        final UUID att1UUID = UUID.randomUUID();

        final Map<String, String> reviewSectionsCompleted = Map.of("subtask", "ACCEPTED");
        final Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> reviewGroups = Map.of(
                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, UnderlyingAgreementReviewDecision.builder().build()
        );
        final Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilityGroups = Map.of(
                "facilityId", UnderlyingAgreementVariationFacilityReviewDecision.builder().build()
        );
        final UnderlyingAgreementVariationPayload unav = UnderlyingAgreementVariationPayload
                .builder()
                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder()
                        .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                        .reason("bla bla bla bla")
                        .build())
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                        .build())
                .build();
        final UnderlyingAgreementVariationSubmitRequestTaskPayload requestTaskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .underlyingAgreement(unav)
                .sectionsCompleted(Map.of("section1", "completed"))
                .underlyingAgreementAttachments(Map.of(att1UUID, "att1"))
                .reviewGroupDecisions(reviewGroups)
                .facilitiesReviewGroupDecisions(facilityGroups)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        Request request = Request.builder()
                .id("1")
                .payload(UnderlyingAgreementVariationRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(requestTaskPayload)
                .build();

        // Invoke
        service.submitUnderlyingAgreementVariation(requestTask, authUser);

        // Verify
        verify(underlyingAgreementVariationPayloadValidatorService, times(1)).validate(requestTask);
        UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        assertThat(requestPayload.getUnderlyingAgreement()).isEqualTo(unav);
        assertThat(requestPayload.getSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("section1", "completed"));
        assertThat(requestPayload.getUnderlyingAgreementAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(att1UUID, "att1"));
        assertThat(requestPayload.getReviewGroupDecisions()).isEqualTo(reviewGroups);
        assertThat(requestPayload.getFacilitiesReviewGroupDecisions()).isEqualTo(facilityGroups);
        assertThat(requestPayload.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);

        UnderlyingAgreementVariationSubmittedRequestActionPayload actionPayload = Mappers
                .getMapper(UnderlyingAgreementVariationSubmitMapper.class).toUnderlyingAgreementVariationSubmittedRequestActionPayload(
                        (UnderlyingAgreementVariationSubmitRequestTaskPayload) requestTask.getPayload(),
                        CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED, authUser.getUserId());
    }
}
