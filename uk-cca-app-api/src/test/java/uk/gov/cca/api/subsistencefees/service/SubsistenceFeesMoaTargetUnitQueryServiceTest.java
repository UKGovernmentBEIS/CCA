package uk.gov.cca.api.subsistencefees.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultsInfo;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaTargetUnitRepository;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaTargetUnitQueryServiceTest {

	@InjectMocks
    private SubsistenceFeesMoaTargetUnitQueryService subsistenceFeesMoaTargetUnitQueryService;

    @Mock
    private SubsistenceFeesMoaTargetUnitRepository subsistenceFeesMoaTargetUnitRepository;
    
    @Test
    void getSubsistenceFeesMoaTargetUnits() {
    	final String businessId = "businessId";
    	final String name = "name";
    	final int page = 0;
        final int pageSize = 30;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
        		.paging(pagingRequest)
        		.build();
        final BigDecimal amount = BigDecimal.valueOf(1000L);
        final SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo = new SubsistenceFeesMoaTargetUnitSearchResultInfo(1L, businessId, name, amount, amount);
        final SubsistenceFeesMoaTargetUnitSearchResultsInfo resultsInfo = SubsistenceFeesMoaTargetUnitSearchResultsInfo.builder()
        		.subsistenceFeesMoaTargetUnitSearchResultInfo(List.of(resultInfo))
        		.total(1L)
        		.build();
        final SubsistenceFeesMoaTargetUnitSearchResultInfoDTO resultInfoDto =
                new SubsistenceFeesMoaTargetUnitSearchResultInfoDTO(1L, businessId, name, FacilityPaymentStatus.IN_PROGRESS, amount);
        final SubsistenceFeesMoaTargetUnitSearchResults expectedResults = SubsistenceFeesMoaTargetUnitSearchResults.builder()
                .subsistenceFeesMoaTargetUnits(List.of(resultInfoDto))
                .total(1L)
                .build();

        when(subsistenceFeesMoaTargetUnitRepository.findBySearchCriteria(1L, criteria)).thenReturn(resultsInfo);

        // invoke
        final SubsistenceFeesMoaTargetUnitSearchResults results = subsistenceFeesMoaTargetUnitQueryService.getSubsistenceFeesMoaTargetUnits(1L, criteria);

        // verify
        verify(subsistenceFeesMoaTargetUnitRepository, times(1)).findBySearchCriteria(1L, criteria);
        assertThat(results).isEqualTo(expectedResults);
    }
    
    @Test
    void getSubsistenceFeesMoaTargetUnitDetailsById() {
    	final String businessId = "businessId";
    	final String name = "name";
        final BigDecimal amount = BigDecimal.valueOf(1000L);
        final LocalDateTime date = LocalDateTime.now();
        final SubsistenceFeesMoaTargetUnitDetailsDTO sfrMoaTargetUnitDetailsDTO = new SubsistenceFeesMoaTargetUnitDetailsDTO(1L, 
        		businessId, name, amount, date, BigDecimal.valueOf(185L), amount, 10L, 10L);

        when(subsistenceFeesMoaTargetUnitRepository.getMoaTargetUnitDetailsById(1L)).thenReturn(Optional.of(sfrMoaTargetUnitDetailsDTO));

        // invoke
        final SubsistenceFeesMoaTargetUnitDetailsDTO result = subsistenceFeesMoaTargetUnitQueryService.getSubsistenceFeesMoaTargetUnitDetailsById(1L);

        // verify
        verify(subsistenceFeesMoaTargetUnitRepository, times(1)).getMoaTargetUnitDetailsById(1L);
        assertThat(result).isEqualTo(sfrMoaTargetUnitDetailsDTO);
    }
    
    @Test
    void getAccountIdByMoaTargetUnitId() {
    	final Long accountId = 10L;
    	final SubsistenceFeesMoaTargetUnit moaTargetUnit = SubsistenceFeesMoaTargetUnit.builder()
    			.id(1L)
    			.accountId(accountId)
    			.build();

        when(subsistenceFeesMoaTargetUnitRepository.findById(1L)).thenReturn(Optional.of(moaTargetUnit));

        // invoke
        final Long result = subsistenceFeesMoaTargetUnitQueryService.getAccountIdByMoaTargetUnitId(1L);

        // verify
        verify(subsistenceFeesMoaTargetUnitRepository, times(1)).findById(1L);
        assertThat(result).isEqualTo(accountId);
    }
}
