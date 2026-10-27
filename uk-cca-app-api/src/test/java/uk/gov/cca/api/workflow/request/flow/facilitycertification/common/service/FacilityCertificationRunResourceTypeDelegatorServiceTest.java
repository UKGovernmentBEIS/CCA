package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunResourceTypeDelegatorServiceTest {

    @InjectMocks
    private FacilityCertificationRunResourceTypeDelegatorService facilityCertificationRunResourceTypeDelegatorService;

    @Mock
    private FacilityCertificationRunAccountService facilityCertificationRunAccountService;

    @Spy
    private ArrayList<FacilityCertificationRunResourceTypeService> facilityCertificationRunResourceTypeServices;

    @BeforeEach
    void setUp() {
        facilityCertificationRunResourceTypeServices.add(facilityCertificationRunAccountService);
    }

    @Test
    void getEligibleAccounts() {
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .build();

        final Map<Long, FacilityCertificationAccountState> accountStateMap = Map.of(
                1L, FacilityCertificationAccountState.builder().accountId(1L).build()
        );

        when(facilityCertificationRunAccountService.getResourceType()).thenReturn(PerformanceDataResourceType.ACCOUNT);
        when(facilityCertificationRunAccountService.getEligibleAccounts(certificationPeriod)).thenReturn(accountStateMap);

        // Invoke
        Map<Long, FacilityCertificationAccountState> result = facilityCertificationRunResourceTypeDelegatorService
                .getEligibleAccounts(certificationPeriod);

        // Verify
        assertThat(result).isEqualTo(accountStateMap);
        verify(facilityCertificationRunAccountService, times(1)).getResourceType();
        verify(facilityCertificationRunAccountService, times(1)).getEligibleAccounts(certificationPeriod);
    }

    @Test
    void getFacilitiesEligibleForCertification() {
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .build();
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .build();

        final Set<Long> facilitiesEligibleForCertification = Set.of(1L);

        when(facilityCertificationRunAccountService.getResourceType()).thenReturn(PerformanceDataResourceType.ACCOUNT);
        when(facilityCertificationRunAccountService.getFacilitiesEligibleForCertification(certificationPeriod, accountState))
                .thenReturn(facilitiesEligibleForCertification);

        // Invoke
        Set<Long> result = facilityCertificationRunResourceTypeDelegatorService
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);

        // Verify
        assertThat(result).isEqualTo(facilitiesEligibleForCertification);
        verify(facilityCertificationRunAccountService, times(1)).getResourceType();
        verify(facilityCertificationRunAccountService, times(1))
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);
    }
}
