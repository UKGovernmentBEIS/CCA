package uk.gov.cca.api.workflow.request.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.transform.AccountReferenceDataMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AccountReferenceDetailsServiceTest {

    @InjectMocks
    private AccountReferenceDetailsService service;

    @Mock
    private TargetUnitAccountService targetUnitAccountService;

    @Mock
    private SectorAssociationInfoService sectorAssociationInfoService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private SectorReferenceDetailsService sectorReferenceDetailsService;

    private static final AccountReferenceDataMapper accountReferenceDataMapper = Mappers.getMapper(AccountReferenceDataMapper.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "accountReferenceDataMapper", accountReferenceDataMapper);
    }

    @Test
    void getTargetUnitAccountDetails() {
        final Long accountId = 1L;

        final TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .name("Test Account")
                .businessId("ADS_1-T00002")
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .subsectorAssociationId(1L)
                .companyRegistrationNumber("6566-6963-8521")
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId)).thenReturn(targetUnitAccountDetailsDTO);

        // invoke
        final TargetUnitAccountDetailsDTO result = service.getTargetUnitAccountDetails(accountId);

        // verify
        assertThat(result.getName()).isEqualTo(targetUnitAccountDetailsDTO.getName());
        assertThat(result.getBusinessId()).isEqualTo(targetUnitAccountDetailsDTO.getBusinessId());
        verify(targetUnitAccountService, times(1))
                .getTargetUnitAccountDetailsById(accountId);
    }

    @Test
    void getSectorAssociationContactByAccountId() {
        final long accountId = 1L;
        final long sectorId = 2L;

        final TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetailsDTO);
        when(sectorAssociationInfoService.getSectorAssociationContact(sectorId))
                .thenReturn(SectorAssociationContactDTO.builder().build());

        // Invoke
        service.getSectorAssociationContactByAccountId(accountId);

        // Verify
        verify(targetUnitAccountService, times(1))
                .getTargetUnitAccountDetailsById(accountId);
        verify(sectorAssociationInfoService, times(1))
                .getSectorAssociationContact(sectorId);
    }

    @Test
    void getAccountReferenceData() {
        final Long subsectorAssociationId = 1L;
        final Long sectorAssociationId = 1L;
        final String subsectorAssociationName = "SUBSECTOR";
        final String measurementUnit = "kWh";
        final String throughputUnit = "tonne";

        final TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO = TargetUnitAccountDetailsDTO.builder()
                .id(1L)
                .name("Test Account")
                .businessId("ADS_1-T00002")
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .sectorAssociationId(sectorAssociationId)
                .subsectorAssociationId(subsectorAssociationId)
                .companyRegistrationNumber("6566-6963-8521")
                .build();

        final MeasurementType measurementType = MeasurementType.getMeasurementTypeByUnit(measurementUnit);

        final SectorAssociationDetails sectorAssociationDetails = SectorAssociationDetails
                .builder()
                .subsectorAssociationName(subsectorAssociationName)
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                		.sectorMeasurementType(measurementType)
                		.sectorThroughputUnit(throughputUnit)
                		.build(), SchemeVersion.CCA_3, SchemeData.builder()
                		.sectorMeasurementType(measurementType)
                		.sectorThroughputUnit(null)
                		.build()))
                .build();

        when(service.getTargetUnitAccountDetails(1L)).thenReturn(targetUnitAccountDetailsDTO);
        when(sectorReferenceDetailsService.getSectorAssociationMeasurementDetails(sectorAssociationId, subsectorAssociationId))
                .thenReturn(sectorAssociationDetails);

        // invoke
        final AccountReferenceData result = service.getAccountReferenceData(1L);

        // verify
        verify(sectorReferenceDetailsService, times(1)).getSectorAssociationMeasurementDetails(sectorAssociationId, subsectorAssociationId);

        assertThat(result.getSectorAssociationDetails().getSubsectorAssociationName()).isEqualTo(subsectorAssociationName);
        assertThat(result.getSectorAssociationDetails().getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorMeasurementType()).isEqualTo(measurementType);
        assertThat(result.getSectorAssociationDetails().getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorThroughputUnit()).isEqualTo(throughputUnit);
        assertThat(result.getSectorAssociationDetails().getSchemeDataMap().get(SchemeVersion.CCA_3).getSectorMeasurementType()).isEqualTo(measurementType);
        assertThat(result.getSectorAssociationDetails().getSchemeDataMap().get(SchemeVersion.CCA_3).getSectorThroughputUnit()).isNull();
        assertThat(result.getTargetUnitAccountDetails().getOperatorType()).isEqualTo(targetUnitAccountDetailsDTO.getOperatorType());
        assertThat(result.getTargetUnitAccountDetails().getCompanyRegistrationNumber()).isEqualTo(targetUnitAccountDetailsDTO.getCompanyRegistrationNumber());
    }
}
