package uk.gov.cca.api.notification.template.repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.cca.api.notification.template.domain.dto.TemplateSearchResults;

public interface DocumentTemplateCustomRepository {

    @Transactional(readOnly = true)
    TemplateSearchResults findBySearchCriteria(DocumentTemplateSearchCriteria searchCriteria);
}
