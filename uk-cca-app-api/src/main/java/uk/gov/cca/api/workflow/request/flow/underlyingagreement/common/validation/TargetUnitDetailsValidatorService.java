package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class TargetUnitDetailsValidatorService {

    private final DataValidator<UnderlyingAgreementTargetUnitDetails> validator;
    private final SubsectorAssociationService subsectorAssociationService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;

    public abstract BusinessValidationResult validate(final RequestTask requestTask);

    public abstract String getPayloadType();

    protected List<UnderlyingAgreementViolation> validateTargetUnitDetails(final Long accountId, final UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        if (ObjectUtils.isEmpty(underlyingAgreementTargetUnitDetails)) {
            violations.add(new UnderlyingAgreementViolation(this.getPayloadType() + UnderlyingAgreementTargetUnitDetails.class.getName(),
                    UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA));
        } else {
            validator.validate(underlyingAgreementTargetUnitDetails).map(businessViolation -> new UnderlyingAgreementViolation(
                            this.getPayloadType() + UnderlyingAgreementTargetUnitDetails.class.getName(),
                            UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA,
                            businessViolation.getData()))
                    .ifPresent(violations::add);

            validateSubsector(accountId, underlyingAgreementTargetUnitDetails.getSubsectorAssociationName(),
                    underlyingAgreementTargetUnitDetails.getSubsectorAssociationId());
        }

        return violations;
    }

    private void validateSubsector(Long accountId, String subsectorAssociationName, Long subsectorAssociationId) {
        Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(accountId);
        List<SubsectorAssociationInfoDTO> subsectorAssociationInfoDTOs = subsectorAssociationService
                .getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId);

        if (CollectionUtils.isEmpty(subsectorAssociationInfoDTOs)) {
            if (subsectorAssociationId != null) {
                throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_TARGET_UNIT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
            }
        } else {
            if (subsectorAssociationId == null ||
                    subsectorAssociationInfoDTOs.stream()
                            .noneMatch(subsector -> subsectorAssociationId.equals(subsector.getId()) &&
                                    subsectorAssociationName.equals(subsector.getName()))) {
                throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_TARGET_UNIT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
            }
        }
    }
}
