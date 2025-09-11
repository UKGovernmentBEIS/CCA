package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FacilityCertificationCreateValidator implements RequestCreateByCAValidator<RequestCreateActionEmptyPayload> {

    private final RequestQueryService requestQueryService;

    @Override
    public RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority, RequestCreateActionEmptyPayload payload) {
        final boolean inProgressExist = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.FACILITY_CERTIFICATION_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);

        if(inProgressExist) {
            return RequestCreateValidationResult.builder()
                    .valid(false)
                    .reportedRequestTypes(Set.of(CcaRequestType.FACILITY_CERTIFICATION_RUN))
                    .build();
        }

        return RequestCreateValidationResult.builder().valid(true).build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.FACILITY_CERTIFICATION_RUN;
    }
}
