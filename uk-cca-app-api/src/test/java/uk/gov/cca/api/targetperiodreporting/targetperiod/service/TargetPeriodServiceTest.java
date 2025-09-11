package uk.gov.cca.api.targetperiodreporting.targetperiod.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.repository.TargetPeriodRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetPeriodServiceTest {

  @InjectMocks
  private TargetPeriodService service;

  @Mock
  private TargetPeriodRepository repository;

  private TargetPeriodType businessId;
  private TargetPeriod targetPeriod;

  @BeforeEach
  void setUp() {
    businessId = TargetPeriodType.TP6;
    targetPeriod = new TargetPeriod();
    targetPeriod.setId(1L);
    targetPeriod.setBusinessId(TargetPeriodType.TP6);
    targetPeriod.setName("targetPeriodName");
  }

  @Test
  void testGetTargetPeriodByBusinessId_found() {
    when(repository.findByBusinessId(businessId)).thenReturn(Optional.of(targetPeriod));

    TargetPeriodDTO result = service.getTargetPeriodByBusinessId(businessId);

    verify(repository).findByBusinessId(businessId);
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(TargetPeriodType.TP6, result.getBusinessId());
  }

  @Test
  void testGetTargetPeriodByBusinessId_notFound() {
    when(repository.findByBusinessId(businessId)).thenReturn(Optional.empty());

    BusinessException thrown = assertThrowsExactly(BusinessException.class,
        () -> service.getTargetPeriodByBusinessId(businessId));

    verify(repository).findByBusinessId(businessId);
    assertEquals(ErrorCode.RESOURCE_NOT_FOUND, thrown.getErrorCode());
  }

  @Test
  void findTargetPeriodNameById() {
    when(repository.findByBusinessId(targetPeriod.getBusinessId())).thenReturn(Optional.of(targetPeriod));

    String resultName = service.findTargetPeriodNameByTargetPeriodType(targetPeriod.getBusinessId());

    verify(repository).findByBusinessId(targetPeriod.getBusinessId());
    assertEquals(targetPeriod.getName(), resultName);
  }

  @Test
  void findTargetPeriodNameById_not_found() {
    when(repository.findByBusinessId(targetPeriod.getBusinessId()))
            .thenReturn(Optional.empty());

    BusinessException thrown = assertThrowsExactly(BusinessException.class,
            () -> service.findTargetPeriodNameByTargetPeriodType(targetPeriod.getBusinessId()));

    verify(repository).findByBusinessId(businessId);
    assertEquals(ErrorCode.RESOURCE_NOT_FOUND, thrown.getErrorCode());
  }

}

