package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_PERIOD_DECLARED;

@Service
public class TargetPeriodDetailsValidatorService {

    public BusinessValidationResult validateCCA2RelatedTargetPeriodsAreEmpty(final UnderlyingAgreementContainer container) {
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

    public BusinessValidationResult validate(final UnderlyingAgreementContainer container, final UnderlyingAgreementContainer originalContainer) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        final TargetPeriod5Details tp5Details = container.getUnderlyingAgreement().getTargetPeriod5Details();
        final TargetPeriod5Details originalTp5Details = originalContainer.getUnderlyingAgreement().getTargetPeriod5Details();
        final TargetPeriod6Details tp6Details = container.getUnderlyingAgreement().getTargetPeriod6Details();
        final TargetPeriod6Details originalTp6Details = originalContainer.getUnderlyingAgreement().getTargetPeriod6Details();

        final boolean hasOldScheme = originalContainer.getUnderlyingAgreement().getFacilities().stream()
                .anyMatch(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().contains(SchemeVersion.CCA_2));
        final boolean needsTpDetails = hasOldScheme && ObjectUtils.isNotEmpty(originalTp5Details) && ObjectUtils.isNotEmpty(originalTp6Details);

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
