package uk.gov.cca.api.web.controller.buyoutsurplus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class BuyOutSurplusRunControllerTest {

	private static final String REQUEST_PATH = "/v1.0/buy-out-surplus/run";

	private MockMvc mockMvc;
	@InjectMocks
	private BuyOutSurplusRunController controller;

	@Mock
	private BuyOutSurplusQueryService buyOutSurplusQueryService;

	@BeforeEach
	void setUp() {
		mockMvc =  MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void getExcludedAccountsForBuyOutSurplusRun() throws Exception {
		final String GET_PATH = REQUEST_PATH + "/excluded-accounts";
		final String accountBusinessId = "accountBusinessId";
		final String accountName = "accountName";
		final Long accountId = 999L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final List<TargetUnitAccountBusinessInfoDTO> targetPeriodExcludedAccountDTOS = List.of(TargetUnitAccountBusinessInfoDTO.builder()
				.name(accountName).businessId(accountBusinessId).accountId(accountId).build());

		when(buyOutSurplusQueryService
				.getAllExcludedEligibleAccountsByTargetPeriod(targetPeriodType)).thenReturn(targetPeriodExcludedAccountDTOS);

		mockMvc.perform(get(GET_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.param("targetPeriodType", targetPeriodType.toString()))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.[0].accountId").value(accountId))
				.andExpect(jsonPath("$.[0].name").value(accountName))
				.andExpect(jsonPath("$.[0].businessId").value(accountBusinessId));


		verify(buyOutSurplusQueryService, times(1))
				.getAllExcludedEligibleAccountsByTargetPeriod(targetPeriodType);
	}
}