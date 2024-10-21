package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmittedRequestActionPayload;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UnderlyingAgreementVariationSubmitMapperTest {

    private final UnderlyingAgreementVariationSubmitMapper mapper = Mappers.getMapper(UnderlyingAgreementVariationSubmitMapper.class);

    @Test
    void toUnderlyingAgreementVariationSubmittedRequestActionPayload() {
        UUID uuid = UUID.randomUUID();

        UnderlyingAgreementVariationPayload unavPayload = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder()
                        .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                        .reason("bla bla bla bla")
                        .build())
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder()
                        .operatorName("test")
                        .build())
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                        .calculatorFile(uuid)
                                        .build())
                                .build())
                        .build())
                .build();

        AccountReferenceData accountData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .throughputUnit("tonne")
                        .build())
                .build();

        UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .underlyingAgreement(unavPayload)
                .accountReferenceData(accountData)
                .underlyingAgreementAttachments(Map.of(uuid, "uuid.pdf"))
                .build();

        UnderlyingAgreementVariationSubmittedRequestActionPayload result =
                mapper.toUnderlyingAgreementVariationSubmittedRequestActionPayload(taskPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_PAYLOAD);

        assertThat(result).isEqualTo(UnderlyingAgreementVariationSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_PAYLOAD)
                .underlyingAgreement(unavPayload)
                .accountReferenceData(accountData)
                .underlyingAgreementAttachments(Map.of(uuid, "uuid.pdf"))
                .build());
    }

}
