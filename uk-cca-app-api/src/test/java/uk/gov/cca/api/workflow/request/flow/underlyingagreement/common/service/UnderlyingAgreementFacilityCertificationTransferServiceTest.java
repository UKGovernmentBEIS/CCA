package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementFacilityCertificationTransferServiceTest {

    @InjectMocks
    private UnderlyingAgreementFacilityCertificationTransferService facilityCertificationTransferService;

    @Mock
    private FacilityCertificationService facilityCertificationService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private CertificationPeriodService certificationPeriodService;

    @Test
    void copyFacilityCertifications() {

        ReflectionTestUtils.setField(facilityCertificationTransferService, "facilityCertificationNewEntrantsStartDate", "2099-01-01");
        Set<String> facilityIds = Set.of("prevFacilityId");

        FacilityBaseInfoDTO prevFacilityData = FacilityBaseInfoDTO.builder()
                .facilityId("prevFacilityId")
                .id(2L)
                .build();

        Set<FacilityBaseInfoDTO> createdFacilities = Set.of(FacilityBaseInfoDTO.builder()
                        .facilityId("newFacilityId")
                        .id(1L)
                        .build(),
                FacilityBaseInfoDTO.builder()
                        .facilityId("newFacilityId2")
                        .id(3L)
                        .build());

        FacilityItem facilityItem1 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder()
                        .previousFacilityId("prevFacilityId")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .build())
                .facilityId("newFacilityId")
                .build();

        FacilityItem facilityItem2 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder()
                        .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                        .build())
                .facilityId("newFacilityId2")
                .build();

        FacilityCertificationDTO previousFacilityCertificationDTO = FacilityCertificationDTO.builder()
                .facilityId(2L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2025, 5, 5))
                .build();

        FacilityCertificationDTO newFacilityCertificationDTO = FacilityCertificationDTO.builder()
                .facilityId(1L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2025, 5, 5))
                .build();

        when(facilityDataQueryService.getFacilityBaseInfoList(Set.of("prevFacilityId")))
                .thenReturn(List.of(prevFacilityData));
        when(facilityCertificationService.getFacilityCertifications(Set.of(2L)))
                .thenReturn(List.of(previousFacilityCertificationDTO));

        // invoke
        facilityCertificationTransferService.processFacilityCertificationsForNewFacilities(createdFacilities, Set.of(facilityItem1, facilityItem2));

        // verify
        verify(facilityDataQueryService, times(1)).getFacilityBaseInfoList(facilityIds);
        verify(facilityCertificationService, times(1)).createFacilityCertifications(List.of(newFacilityCertificationDTO));
    }

    @Test
    void copyFacilityCertifications_without_previous_facility_certifications() {

        ReflectionTestUtils.setField(facilityCertificationTransferService, "facilityCertificationNewEntrantsStartDate", "2099-01-01");
        Set<String> facilityIds = Set.of("prevFacilityId");

        FacilityBaseInfoDTO prevFacilityData = FacilityBaseInfoDTO.builder()
                .facilityId("prevFacilityId")
                .id(2L)
                .build();

        Set<FacilityBaseInfoDTO> createdFacilities = Set.of(FacilityBaseInfoDTO.builder()
                        .facilityId("newFacilityId")
                        .id(1L)
                        .build(),
                FacilityBaseInfoDTO.builder()
                        .facilityId("newFacilityId2")
                        .id(3L)
                        .build());

        FacilityItem facilityItem1 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder()
                        .previousFacilityId("prevFacilityId")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .build())
                .facilityId("newFacilityId")
                .build();

        FacilityItem facilityItem2 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder()
                        .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                        .build())
                .facilityId("newFacilityId2")
                .build();

        when(facilityDataQueryService.getFacilityBaseInfoList(Set.of("prevFacilityId")))
                .thenReturn(List.of(prevFacilityData));
        when(facilityCertificationService.getFacilityCertifications(Set.of(2L)))
                .thenReturn(Collections.emptyList());

        // invoke
        facilityCertificationTransferService.processFacilityCertificationsForNewFacilities(createdFacilities, Set.of(facilityItem1, facilityItem2));

        // verify
        verify(facilityDataQueryService, times(1)).getFacilityBaseInfoList(facilityIds);
        verify(facilityCertificationService, never()).createFacilityCertifications(List.of());
    }

    @Test
    void processFacilityCertificationsForNewFacilities_without_CHANGE_OF_OWNERSHIP() {

        ReflectionTestUtils.setField(facilityCertificationTransferService, "facilityCertificationNewEntrantsStartDate", "2025-01-01");
        CertificationPeriodInfoDTO currentCP = CertificationPeriodInfoDTO.builder()
                .id(2L)
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 7, 31))
                .build();
        Set<FacilityBaseInfoDTO> createdFacilities = Set.of(FacilityBaseInfoDTO.builder()
                        .facilityId("newFacilityId")
                        .id(1L)
                        .build(),
                FacilityBaseInfoDTO.builder()
                        .facilityId("newFacilityId2")
                        .id(2L)
                        .build());

        FacilityItem facilityItem1 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder()
                        .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                        .build())
                .facilityId("newFacilityId")
                .build();

        FacilityItem facilityItem2 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder()
                        .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                        .build())
                .facilityId("newFacilityId2")
                .build();

        when(facilityDataQueryService.getFacilityBaseInfoList(Set.of())).thenReturn(List.of());
        when(facilityCertificationService.getFacilityCertifications(Set.of())).thenReturn(List.of());
	    when(certificationPeriodService.getCurrentCertificationPeriodOptional())
			    .thenReturn(Optional.of(currentCP));
        // invoke
        facilityCertificationTransferService.processFacilityCertificationsForNewFacilities(createdFacilities, Set.of(facilityItem1, facilityItem2));

        // verify
        verify(facilityDataQueryService, times(1)).getFacilityBaseInfoList(Set.of());
        verify(facilityCertificationService, times(1))
                .createFacilityCertifications(anyList());
    }


    @Test
    void processFacilityCertificationsForNewFacilities() {

        ReflectionTestUtils.setField(facilityCertificationTransferService, "facilityCertificationNewEntrantsStartDate", "2025-01-01");

        FacilityBaseInfoDTO facilityDto = new FacilityBaseInfoDTO();
        facilityDto.setFacilityId("FAC-123");
        facilityDto.setId(1L);

        FacilityDetails details = new FacilityDetails();
        details.setApplicationReason(ApplicationReasonType.NEW_AGREEMENT);

        FacilityItem facilityItem = mock(FacilityItem.class);
        when(facilityItem.getFacilityId()).thenReturn("FAC-123");
        when(facilityItem.getFacilityDetails()).thenReturn(details);

        Set<FacilityBaseInfoDTO> createdFacilities = Set.of(facilityDto);
        Set<FacilityItem> facilityItems = Set.of(facilityItem);

        CertificationPeriodInfoDTO certificationPeriod = new CertificationPeriodInfoDTO();
        certificationPeriod.setId(999L);

        when(certificationPeriodService.getCurrentCertificationPeriodOptional())
		        .thenReturn(Optional.of(certificationPeriod));

        facilityCertificationTransferService.processFacilityCertificationsForNewFacilities(createdFacilities, facilityItems);

        ArgumentCaptor<List<FacilityCertificationDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(facilityCertificationService).createFacilityCertifications(captor.capture());

        List<FacilityCertificationDTO> created = captor.getValue();
        assertEquals(1, created.size());
        assertEquals(1L, created.getFirst().getFacilityId());
        assertEquals(999L, created.getFirst().getCertificationPeriodId());
        assertEquals(FacilityCertificationStatus.CERTIFIED, created.getFirst().getCertificationStatus());
    }


}
