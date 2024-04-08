package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.config.AppProperties;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.user.regulator.service.RegulatorUserAuthService;


@Service
public class TestDocumentTemplateCommonParamsAbstractProvider extends DocumentTemplateCommonParamsAbstractProvider {

    public TestDocumentTemplateCommonParamsAbstractProvider(RegulatorUserAuthService regulatorUserAuthService, UserAuthService userAuthService,
                                                        AppProperties appProperties, DateService dateService,
                                                        CompetentAuthorityService competentAuthorityService) {
        super(regulatorUserAuthService, userAuthService, appProperties, dateService, competentAuthorityService);

    }

    @Override
    public String getPermitReferenceId(Long accountId) {
        return null;
    }

    @Override
    public AccountTemplateParams getAccountTemplateParams(Long accountId) {
        return null;
    }
}
