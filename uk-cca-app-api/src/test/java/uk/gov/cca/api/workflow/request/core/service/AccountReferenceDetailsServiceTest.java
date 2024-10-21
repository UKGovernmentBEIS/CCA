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
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.transform.AccountReferenceDataMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private SectorAssociationSchemeService sectorAssociationSchemeService;

    private static final AccountReferenceDataMapper accountReferenceDataMapper = Mappers.getMapper(AccountReferenceDataMapper.class);

    @BeforeEach
    public void init() {
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
    void getSectorAssociationNameByAccountId() {
        final long accountId = 1L;
        final long sectorId = 2L;

        final TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetailsDTO);
        when(sectorAssociationQueryService.getSectorAssociationName(sectorId))
                .thenReturn("Name");

        // Invoke
        service.getSectorAssociationNameByAccountId(accountId);

        // Verify
        verify(targetUnitAccountService, times(1))
                .getTargetUnitAccountDetailsById(accountId);
        verify(sectorAssociationQueryService, times(1))
                .getSectorAssociationName(sectorId);
    }
    
    @Test
    void getSectorAssociationIdentifierByAccountId() {
        final long accountId = 1L;
        final long sectorId = 2L;

        final TargetUnitAccountDetailsDTO targetUnitAccountDetailsDTO = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();

        when(targetUnitAccountService.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetailsDTO);
        when(sectorAssociationQueryService.getSectorAssociationIdentifier(sectorId))
                .thenReturn("identifier");

        // Invoke
        service.getSectorAssociationIdentifierByAccountId(accountId);

        // Verify
        verify(targetUnitAccountService, times(1))
                .getTargetUnitAccountDetailsById(accountId);
        verify(sectorAssociationQueryService, times(1))
                .getSectorAssociationIdentifier(sectorId);
    }
    
    @Test
    void getSectorAssociationMeasurementInfo() {
        final long sectorId = 2L;
        final long subSectorId = 5L;

        when(sectorAssociationInfoService.getSectorAssociationMeasurementInfo(sectorId, subSectorId))
                .thenReturn(SectorAssociationMeasurementInfoDTO.builder().build());

        // Invoke
        service.getSectorAssociationMeasurementInfo(sectorId, subSectorId);

        // Verify
        verify(sectorAssociationInfoService, times(1))
                .getSectorAssociationMeasurementInfo(sectorId, subSectorId);
    }
    
    @Test
    void getSectorAssociationSchemeBySectorAssociationId() {
        final long sectorId = 2L;

        when(sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorId))
                .thenReturn(SectorAssociationSchemeDTO.builder().build());

        // Invoke
        service.getSectorAssociationSchemeBySectorAssociationId(sectorId);

        // Verify
        verify(sectorAssociationSchemeService, times(1))
                .getSectorAssociationSchemeBySectorAssociationId(sectorId);
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

		final SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO = SectorAssociationMeasurementInfoDTO
				.builder()
				.subsectorAssociationName(subsectorAssociationName)
				.measurementUnit(measurementUnit)
				.throughputUnit(throughputUnit)
				.build();

        when(service.getTargetUnitAccountDetails(1L)).thenReturn(targetUnitAccountDetailsDTO);
        when(sectorAssociationInfoService.getSectorAssociationMeasurementInfo(sectorAssociationId, subsectorAssociationId)).thenReturn(sectorAssociationMeasurementInfoDTO);

        // invoke
        final AccountReferenceData result = service.getAccountReferenceData(1L);

        // verify
        verify(sectorAssociationInfoService, times(1)).getSectorAssociationMeasurementInfo(sectorAssociationId, subsectorAssociationId);

        assertThat(result.getSectorAssociationDetails().getSubsectorAssociationName()).isEqualTo(subsectorAssociationName);
        assertThat(result.getSectorAssociationDetails().getMeasurementType()).isEqualTo(measurementType);
        assertThat(result.getSectorAssociationDetails().getThroughputUnit()).isEqualTo(throughputUnit);
        assertThat(result.getTargetUnitAccountDetails().getOperatorType()).isEqualTo(targetUnitAccountDetailsDTO.getOperatorType());
        assertThat(result.getTargetUnitAccountDetails().getCompanyRegistrationNumber()).isEqualTo(targetUnitAccountDetailsDTO.getCompanyRegistrationNumber());
    }

}
