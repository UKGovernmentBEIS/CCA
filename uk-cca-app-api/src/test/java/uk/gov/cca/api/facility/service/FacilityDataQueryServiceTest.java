package uk.gov.cca.api.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityDataQueryServiceTest {

	@InjectMocks
    private FacilityDataQueryService service;

    @Mock
    private FacilityDataRepository repository;

    @Test
    void isExistingFacilityId() {
        String facilityId = "facilityId";
        when(repository.existsByFacilityId(facilityId)).thenReturn(false);

        boolean result = service.isExistingFacilityId(facilityId);

        assertThat(result).isFalse();
        verify(repository, times(1)).existsByFacilityId(facilityId);
    }

    @Test
    void isActiveFacility() {
    	String facilityId = "facilityId";
        when(repository.existsByFacilityIdAndClosedDateIsNull(facilityId)).thenReturn(false);

        boolean result = service.isActiveFacility(facilityId);

        assertThat(result).isFalse();
        verify(repository, times(1)).existsByFacilityIdAndClosedDateIsNull(facilityId);
    }

    @Test
    void findActiveFacilitiesByAccountId() {
        Long accountId = 1L;

        FacilityData activeFacility = FacilityData.builder()
                .facilityId("activeFacility").accountId(accountId).closedDate(null).createdDate(LocalDateTime.now()).build();

        List<FacilityData> facilities = List.of(activeFacility);

        when(repository.findFacilityDataByAccountIdAndClosedDateIsNull(accountId)).thenReturn(facilities);

        List<FacilityData> results = service.findActiveFacilitiesByAccountId(accountId);

        assertThat(results).contains(activeFacility);
    }
}
