package uk.gov.cca.api.workflow.request.core.transform;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;

import static org.assertj.core.api.Assertions.assertThat;

class AccountReferenceDataMapperTest {

    private final AccountReferenceDataMapper mapper = Mappers.getMapper(AccountReferenceDataMapper.class);

    @Test
    void toAccountReferenceData() {
        final String subsectorAssociationName = "SubSectorName";
        final MeasurementType measurementType = MeasurementType.CARBON_KG;
        final String throughputUnit = "tonne";
        
        final TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO = TargetUnitAccountDetailsDTO.builder()
                .id(1L)
                .name("Test Account")
                .businessId("ADS_1-T00002")
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .sectorAssociationId(1L)
                .subsectorAssociationId(1L)
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .companyRegistrationNumber("6566-6963-8521")
                .responsiblePerson(getResponsiblePersonDTO())
                .build();
        
		final SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO = SectorAssociationMeasurementInfoDTO
				.builder()
				.subsectorAssociationName(subsectorAssociationName)
				.measurementUnit(measurementType.getUnit())
				.throughputUnit(throughputUnit)
				.build();

        final AccountReferenceData result = mapper.toAccountReferenceData(targetUnitAccountDetailsDTO, sectorAssociationMeasurementInfoDTO);

        assertThat(result.getSectorAssociationDetails().getSubsectorAssociationName()).isEqualTo(subsectorAssociationName);
        assertThat(result.getSectorAssociationDetails().getMeasurementType()).isEqualTo(measurementType);
        assertThat(result.getSectorAssociationDetails().getThroughputUnit()).isEqualTo(throughputUnit);
        assertThat(result.getTargetUnitAccountDetails().getOperatorType()).isEqualTo(targetUnitAccountDetailsDTO.getOperatorType());
        assertThat(result.getTargetUnitAccountDetails().getCompanyRegistrationNumber()).isEqualTo(targetUnitAccountDetailsDTO.getCompanyRegistrationNumber());
        assertThat(result.getTargetUnitAccountDetails().getResponsiblePerson().getFirstName()).isEqualTo(targetUnitAccountDetailsDTO.getResponsiblePerson().getFirstName());
        assertThat(result.getTargetUnitAccountDetails().getAdministrativeContactDetails().getJobTitle()).isEqualTo(targetUnitAccountDetailsDTO.getResponsiblePerson().getJobTitle());
        assertThat(result.getTargetUnitAccountDetails().getSectorAssociationId()).isEqualTo(targetUnitAccountDetailsDTO.getSectorAssociationId());
        assertThat(result.getTargetUnitAccountDetails().getSubsectorAssociationId()).isEqualTo(targetUnitAccountDetailsDTO.getSubsectorAssociationId());


}

    @NotNull
    private static TargetUnitAccountContactDTO getAdministrativeContactDetailsDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .address(AccountAddressDTO.builder().build()).email("email").build();
    }

    @NotNull
    private static TargetUnitAccountContactDTO getResponsiblePersonDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder().countryCode("code").number("number").build()).build();
    }

}
