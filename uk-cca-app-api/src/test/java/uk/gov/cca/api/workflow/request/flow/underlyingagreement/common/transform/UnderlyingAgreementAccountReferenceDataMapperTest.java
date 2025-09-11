package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementAccountReferenceDataMapperTest {

    private final UnderlyingAgreementAccountReferenceDataMapper mapper = Mappers.getMapper(UnderlyingAgreementAccountReferenceDataMapper.class);

    @Test
    void toTargetUnitAccountDTO_with_Target_Unit_Details() {
        Long sectorAssociationId = 1L;

        final UnderlyingAgreementTargetUnitDetails actual = UnderlyingAgreementTargetUnitDetails
                .builder()
                .operatorName("name")
                .operatorAddress(AccountAddressDTO.builder().postcode("CB111").build())
                .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder().email("email@email.com").build())
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .isCompanyRegistrationNumber(Boolean.TRUE)
                .registrationNumberMissingReason("reason")
                .subsectorAssociationName("name")
                .subsectorAssociationId(sectorAssociationId)
                .build();

        TargetUnitAccountUpdateDTO result = mapper.toTargetUnitAccountUpdateDTO(actual);

        assertThat(actual.getResponsiblePersonDetails().getEmail()).isEqualTo(result.getResponsiblePerson().getEmail());
        assertThat(actual.getOperatorAddress().getPostcode()).isEqualTo(result.getOperatorAddress().getPostcode());
        assertThat(actual.getOperatorType()).isEqualTo(result.getOperatorType());
        assertThat(actual.getRegistrationNumberMissingReason()).isEqualTo(result.getRegistrationNumberMissingReason());
    }

}
