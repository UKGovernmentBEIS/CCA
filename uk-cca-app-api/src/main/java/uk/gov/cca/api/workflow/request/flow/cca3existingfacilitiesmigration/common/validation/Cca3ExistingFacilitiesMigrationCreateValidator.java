package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation;

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
public class Cca3ExistingFacilitiesMigrationCreateValidator implements RequestCreateByCAValidator<RequestCreateActionEmptyPayload> {

    private final RequestQueryService requestQueryService;

    @Override
    public RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority, RequestCreateActionEmptyPayload payload) {
        final boolean inProgressExist = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                this.getRequestType(), RequestStatuses.IN_PROGRESS, competentAuthority);

        if(inProgressExist) {
            return RequestCreateValidationResult.builder()
                    .valid(false)
                    .reportedRequestTypes(Set.of(this.getRequestType()))
                    .build();
        }

        return RequestCreateValidationResult.builder().valid(true).build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN;
    }
}
