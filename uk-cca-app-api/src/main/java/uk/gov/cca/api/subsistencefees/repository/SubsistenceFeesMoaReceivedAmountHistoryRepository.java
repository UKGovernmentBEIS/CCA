package uk.gov.cca.api.subsistencefees.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistory;

import java.util.List;

@Repository
public interface SubsistenceFeesMoaReceivedAmountHistoryRepository extends JpaRepository<SubsistenceFeesMoaReceivedAmountHistory, Long> {

    @Transactional(readOnly = true)
    @Query(value = "select hs from SubsistenceFeesMoaReceivedAmountHistory hs where hs.subsistenceFeesMoa.id = :moaId")
    List<SubsistenceFeesMoaReceivedAmountHistory> findByMoaId(Long moaId);
}
