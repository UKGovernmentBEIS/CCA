package uk.gov.cca.api.notification.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.notification.template.domain.DocumentTemplate;
import uk.gov.cca.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Optional;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long>, DocumentTemplateCustomRepository {

    Optional<DocumentTemplate> findByTypeAndCompetentAuthority(DocumentTemplateType type,
                                                               CompetentAuthorityEnum competentAuthority);
}
