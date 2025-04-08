package uk.gov.cca.api.subsistencefees.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

@Repository
public interface FacilityProcessStatusRepository extends JpaRepository<FacilityProcessStatus, Long> {

    @Transactional(readOnly = true)
    @Query(name = FacilityProcessStatus.NAMED_QUERY_FIND_ACCOUNTS_FOR_SUBSISTENCE_FEES_RUN)
    Set<Long> findTargetUnitAccountsForSubsistenceFeesRun(Year chargingYear, LocalDate firstDateOfChargingYear, LocalDate endDateOfChargingYear);

    @Transactional(readOnly = true)
    @Query(name = FacilityProcessStatus.NAMED_QUERY_GET_SECTOR_FACILITIES_FOR_SUBSISTENCE_FEES_RUN)
    List<EligibleFacilityDTO> findSectorFacilitiesForSubsistenceFeesRun(long sectorAssociationId, Year chargingYear, LocalDate firstDateOfChargingYear, LocalDate endDateOfChargingYear);

    @Transactional(readOnly = true)
    @Query(name = FacilityProcessStatus.NAMED_QUERY_GET_ACCOUNT_FACILITIES_FOR_SUBSISTENCE_FEES_RUN)
    List<EligibleFacilityDTO> findAccountFacilitiesForSubsistenceFeesRun(long accountId, Year chargingYear, LocalDate firstDateOfChargingYear, LocalDate endDateOfChargingYear);
}
