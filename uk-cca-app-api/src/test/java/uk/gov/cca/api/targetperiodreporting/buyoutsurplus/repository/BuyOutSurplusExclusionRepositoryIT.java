package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusExclusion;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
class BuyOutSurplusExclusionRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private BuyOutSurplusExclusionRepository repository;

	private Long accountId;

	private BuyOutSurplusExclusion buyOutSurplusExclusion;

	@BeforeEach
	void setUp() {
		accountId = 999L;
		buyOutSurplusExclusion = BuyOutSurplusExclusion.builder().accountId(accountId).build();
		buyOutSurplusExclusion.setAccountId(accountId);
		repository.saveAndFlush(buyOutSurplusExclusion);
	}

	@Test
	void testExistsByAccountId() {

		boolean exists = repository.existsByAccountId(accountId);
		boolean notExists = repository.existsByAccountId(2L);

		assertThat(exists).isTrue();
		assertThat(notExists).isFalse();

	}

	@Test
	void deleteByAccountId() {
		Long id = buyOutSurplusExclusion.getId();

		repository.deleteByAccountId(accountId);

		Optional<BuyOutSurplusExclusion> optional = repository.findById(id);

		assertEquals(Optional.empty(), optional);
	}


}