package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurplusQueryServiceTest {

	@InjectMocks
	private SurplusQueryService surplusQueryService;

	@Mock
	private SurplusRepository surplusRepository;

	@Test
	void getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId() {
		final long accountId = 999L;
		final List<SurplusGainedDTO> surplusGainedDTOList = List.of(
				SurplusGainedDTO.builder()
						.surplusGained(BigDecimal.TEN)
						.hasHistory(false)
						.targetPeriod(TargetPeriodType.TP6)
						.build()
		);

		when(surplusRepository.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId))
				.thenReturn(surplusGainedDTOList);

		final List<SurplusGainedDTO> result = surplusQueryService.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId);

		Assertions.assertEquals(result, surplusGainedDTOList);
		verify(surplusRepository, times(1)).getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId);
	}

	@Test
	void getAllSurplusHistoryByAccountIdAndTargetPeriod() {
		long accountId = 999L;
		final SurplusEntity entity = SurplusEntity.builder()
				.surplusHistoryList(List.of(
						SurplusHistory.builder()
								.newSurplusGained(BigDecimal.TEN)
								.submitter("Regulator")
								.comments("comments")
								.build()
				))
				.build();

		when(surplusRepository.findByAccountIdAndTargetPeriod_BusinessId(accountId, TargetPeriodType.TP6))
				.thenReturn(Optional.of(entity));


		final List<SurplusHistoryDTO> result = surplusQueryService
				.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId);

		Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getNewSurplusGained(), result.getFirst().getSurplusGained());
		Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getComments(), result.getFirst().getComments());
		Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getSubmitter(), result.getFirst().getSubmitter());
		verify(surplusRepository, times(1))
				.findByAccountIdAndTargetPeriod_BusinessId(accountId, TargetPeriodType.TP6);
	}

	@Test
	void getAllSurplusHistoryByAccountIdAndTargetPeriod_empty() {
		long accountId = 999L;

		when(surplusRepository.findByAccountIdAndTargetPeriod_BusinessId(accountId, TargetPeriodType.TP6))
				.thenReturn(Optional.empty());

		final List<SurplusHistoryDTO> result = surplusQueryService
				.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId);

		assertThat(result).isEmpty();
		verify(surplusRepository, times(1))
				.findByAccountIdAndTargetPeriod_BusinessId(accountId, TargetPeriodType.TP6);
	}
}