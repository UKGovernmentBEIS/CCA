package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import uk.gov.cca.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.cca.api.workflow.request.core.domain.Request;

public interface DocumentTemplateCommonParamsProvider {

    TemplateParams constructCommonTemplateParams(final Request request, final String signatory);
}
