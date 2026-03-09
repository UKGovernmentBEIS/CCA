package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_PERIOD_DECLARED;

@Service
@RequiredArgsConstructor
public class CCA2BaselineAndTargetsValidatorService {

	private final UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;
	
    public BusinessValidationResult validateEmpty(final UnderlyingAgreementContainer container) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        final TargetPeriod5Details tp5Details = container.getUnderlyingAgreement().getTargetPeriod5Details();
        final TargetPeriod6Details tp6Details = container.getUnderlyingAgreement().getTargetPeriod6Details();

        if(!ObjectUtils.isEmpty(tp5Details)) {
            violations.add(new UnderlyingAgreementViolation(TargetPeriod5Details.class.getName(), INVALID_TARGET_PERIOD_DECLARED));
        }

        if(!ObjectUtils.isEmpty(tp6Details)) {
            violations.add(new UnderlyingAgreementViolation(TargetPeriod6Details.class.getName(), INVALID_TARGET_PERIOD_DECLARED));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validate(
    		final UnderlyingAgreementContainer container, 
    		final UnderlyingAgreementContainer originalContainer, 
    		LocalDate requestCreationDate) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        final TargetPeriod5Details tp5Details = container.getUnderlyingAgreement().getTargetPeriod5Details();
        final TargetPeriod6Details tp6Details = container.getUnderlyingAgreement().getTargetPeriod6Details();

        final boolean needsTpDetails = underlyingAgreementSchemeVersionsHelperService.shouldShowTp5Tp6(originalContainer, requestCreationDate);

        if((needsTpDetails && ObjectUtils.isEmpty(tp5Details))
                || (!needsTpDetails && ObjectUtils.isNotEmpty(tp5Details))) {
            violations.add(new UnderlyingAgreementViolation(TargetPeriod5Details.class.getName(), INVALID_TARGET_PERIOD_DECLARED));
        }

        if((needsTpDetails && ObjectUtils.isEmpty(tp6Details))
                || (!needsTpDetails && ObjectUtils.isNotEmpty(tp6Details))) {
            violations.add(new UnderlyingAgreementViolation(TargetPeriod6Details.class.getName(), INVALID_TARGET_PERIOD_DECLARED));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
