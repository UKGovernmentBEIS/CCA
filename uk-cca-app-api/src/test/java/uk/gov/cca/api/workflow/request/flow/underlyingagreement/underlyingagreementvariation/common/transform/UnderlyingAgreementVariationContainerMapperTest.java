package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementVariationContainerMapperTest {

    private final UnderlyingAgreementVariationContainerMapper mapper = Mappers.getMapper(UnderlyingAgreementVariationContainerMapper.class);

    @Test
    void toUnderlyingAgreementContainer() {
        UUID uuid = UUID.randomUUID();

        UnderlyingAgreement una = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .build();

        AccountReferenceData accountData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
                				.sectorThroughputUnit("tonne")
                				.build()))
                        .build())
                .build();

        UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(una)
                        .build())
                .accountReferenceData(accountData)
                .build();


        UnderlyingAgreementContainer result = mapper.toUnderlyingAgreementContainer(taskPayload);

        assertThat(result).isEqualTo(UnderlyingAgreementContainer.builder()
                .underlyingAgreement(una)
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("tonne")
        				.build()))
                .build());
    }

}
