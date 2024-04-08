package uk.gov.cca.api.workflow.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.payment.domain.PaymentMethod;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    @Transactional(readOnly = true)
    List<PaymentMethod> findByCompetentAuthority(CompetentAuthorityEnum competentAuthority);
}
