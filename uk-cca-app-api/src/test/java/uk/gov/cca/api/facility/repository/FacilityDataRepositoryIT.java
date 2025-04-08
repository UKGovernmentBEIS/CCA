package uk.gov.cca.api.facility.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class FacilityDataRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FacilityDataRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        FacilityAddress address1 = FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address1);

        FacilityAddress address2 = FacilityAddress.builder()
                .line1("456 Test Avenue")
                .city("Another City")
                .postcode("67890")
                .country("Another Country")
                .build();
        entityManager.persist(address2);

        FacilityAddress address3 = FacilityAddress.builder()
                .line1("789 Test Road")
                .city("Third City")
                .postcode("54321")
                .country("Third Country")
                .build();
        entityManager.persist(address3);

        final FacilityData facility1 = FacilityData.builder()
                .facilityId("ADS_1-F00014")
                .accountId(1L)
                .siteName("site1")
                .address(address1)
                .createdDate(LocalDateTime.now())
                .build();
        entityManager.persist(facility1);

        final FacilityData facility2 = FacilityData.builder()
                .facilityId("ADS_1-F00015")
                .accountId(2L)
                .siteName("facil2")
                .address(address2)
                .createdDate(LocalDateTime.now())
                .build();
        entityManager.persist(facility2);

        final FacilityData facility3 = FacilityData.builder()
                .facilityId("ADS_1-F00016")
                .accountId(1L)
                .siteName("terminal3")
                .address(address3)
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .createdDate(LocalDateTime.of(2023, 8, 1, 12, 0))
                .build();
        entityManager.persist(facility3);

        flushAndClear();
    }

    @Test
    void findByFacilityId() {
        final String facilityId = "ADS_1-F00016";

        Optional<FacilityData> facilityData = repository.findByFacilityId(facilityId);

        assertThat(facilityData).isPresent();
    }

    @Test
    void existsByFacilityId() {
        final String facilityId = "ADS_1-F00016";

        boolean exists = repository.existsByFacilityId(facilityId);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByFacilityIdAndClosedDateIsNull() {
        final String facilityId = "ADS_1-F00016";

        boolean exists = repository.existsByFacilityIdAndClosedDateIsNull(facilityId);

        assertThat(exists).isFalse();
    }

    @Test
    void findAllByFacilityIdIn() {
        final String facilityId1 = "ADS_1-F00015";
        final String facilityId2 = "ADS_1-F00016";

        List<FacilityData> facilities = repository.findAllByFacilityIdIn(Set.of(facilityId1, facilityId2));

        assertThat(facilities).hasSize(2);
        assertThat(facilities.stream().map(FacilityData::getFacilityId).collect(Collectors.toSet()))
                .isEqualTo(Set.of(facilityId1, facilityId2));
    }

    @Test
    void findFacilityDataByAccountIdAndClosedDateIsNull() {
        List<FacilityData> facilities = repository.findFacilityDataByAccountIdAndClosedDateIsNull(1L);

        assertThat(facilities).hasSize(1);
        assertThat(facilities.getFirst().getFacilityId()).isEqualTo("ADS_1-F00014");
    }

    @Test
    void searchFacilityDataByAccountIdAndTerm() {
        final int pageSize = 30;
        final Pageable pageable = PageRequest.of(0, pageSize, Sort.by("facilityId"));

        Page<FacilityData> results = repository.searchFacilityDataByAccountIdAndTerm(pageable, 1L, "term");

        List<String> facilities = results.stream().map(FacilityData::getFacilityId).toList();
        assertThat(facilities).hasSize(1);
        assertThat(facilities.getFirst()).isEqualTo("ADS_1-F00016");
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        flushAndClear();
    }


    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
