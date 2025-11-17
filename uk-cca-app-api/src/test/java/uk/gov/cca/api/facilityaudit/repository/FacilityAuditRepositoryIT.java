package uk.gov.cca.api.facilityaudit.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.facilityaudit.domain.FacilityAudit;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class FacilityAuditRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private FacilityAuditRepository repository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void shouldFindFacilityAuditByFacilityId() {
		Long facilityId = 123L;

		FacilityAudit audit = new FacilityAudit();
		audit.setFacilityId(facilityId);
		entityManager.persist(audit);

		flushAndClear();

		Optional<FacilityAudit> result = repository.findFacilityAuditByFacilityId(facilityId);

		assertThat(result).isPresent();
		assertEquals(facilityId, result.get().getFacilityId());
	}

	@Test
	void shouldReturnEmptyWhenFacilityAuditNotFound() {

		Optional<FacilityAudit> result = repository.findFacilityAuditByFacilityId(999L);

		assertTrue(result.isEmpty());
	}

	@Test
	void findAllByAuditRequiredIsTrueAndFacilityIdIn() {
		Long facilityId1 = 123L;
		Long facilityId2 = 456L;
		Long facilityId3 = 789L;

		FacilityAudit audit1 = new FacilityAudit();
		audit1.setFacilityId(facilityId1);
		audit1.setAuditRequired(true);

		FacilityAudit audit2 = new FacilityAudit();
		audit2.setFacilityId(facilityId2);
		audit2.setAuditRequired(false);

		entityManager.persist(audit1);
		entityManager.persist(audit2);
		flushAndClear();

		Set<FacilityAudit> auditRequiredFacilityIds = repository.findAllByAuditRequiredIsTrueAndFacilityIdIn(Set.of(facilityId1, facilityId2, facilityId3));

		assertThat(auditRequiredFacilityIds)
				.hasSize(1)
				.contains(audit1);
	}

	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}
