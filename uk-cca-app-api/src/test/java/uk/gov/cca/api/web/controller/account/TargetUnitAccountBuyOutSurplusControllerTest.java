package uk.gov.cca.api.web.controller.account;

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
import uk.gov.cca.api.web.controller.account.buyoutsurplus.TargetUnitAccountBuyOutSurplusController;
import uk.gov.cca.api.web.orchestrator.account.service.TargetUnitAccountBuyOutSurplusServiceOrchestrator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class TargetUnitAccountBuyOutSurplusControllerTest {

	private static final String REQUEST_PATH = "/v1.0/target-unit-accounts/{accountId}/buy-out-surplus/";

	private MockMvc mockMvc;
	@InjectMocks
	private TargetUnitAccountBuyOutSurplusController controller;

	@Mock
	private TargetUnitAccountBuyOutSurplusServiceOrchestrator targetUnitAccountBuyOutSurplusServiceOrchestrator;

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void getTargetUnitAccountBuyOutFeesExcluded() throws Exception {

		final long accountId = 1L;
		final String GET_PATH = REQUEST_PATH.replace("{accountId}", String.valueOf(accountId)) + "/excluded";

		when(targetUnitAccountBuyOutSurplusServiceOrchestrator.isAccountExcludedFromBuyOutSurplus(accountId))
				.thenReturn(true);

		mockMvc.perform(get(GET_PATH)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$").value(true));

		verify(targetUnitAccountBuyOutSurplusServiceOrchestrator,times(1))
				.isAccountExcludedFromBuyOutSurplus(accountId);
	}
}