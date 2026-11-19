package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.netz.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class SurplusValidatorServiceTest {
    
    @InjectMocks
    private SurplusValidatorService surplusValidatorService;
    
    @Test
    void validateNoDuplicateResourceIds_whenResourceIdsAreUnique_doesNotThrow() {
        // Arrange
        List<SurplusCreate> entries = List.of(
                createSurplusCreate(1L),
                createSurplusCreate(2L),
                createSurplusCreate(3L)
        );

        assertDoesNotThrow(() -> surplusValidatorService.validateNoDuplicateResourceIds(entries));
    }
    
    @Test
    void validateNoDuplicateResourceIds_whenEntriesAreEmpty_doesNotThrow() {

        assertDoesNotThrow(() -> surplusValidatorService.validateNoDuplicateResourceIds(List.of()));
    }
    
    @Test
    void validateNoDuplicateResourceIds_whenResourceIdIsDuplicated_throwsException() {
        // Arrange
        List<SurplusCreate> entries = List.of(
                createSurplusCreate(1L),
                createSurplusCreate(2L),
                createSurplusCreate(1L)
        );
        
        BusinessException ex = assertThrows(BusinessException.class,
                () -> surplusValidatorService.validateNoDuplicateResourceIds(entries));

        assertThat(ex).isInstanceOf(BusinessException.class);
    }
    
    private SurplusCreate createSurplusCreate(Long resourceId) {
        return SurplusCreate.builder()
                .resourceId(resourceId)
                .build();
    }
}
