package uk.gov.cca.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.RequestNoteAuthorityInfoProvider;
import uk.gov.cca.api.workflow.request.core.repository.RequestNoteRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.Request;

@RequiredArgsConstructor
@Service
public class RequestNoteAuthorityInfoQueryService implements RequestNoteAuthorityInfoProvider {

    private final RequestNoteRepository requestNoteRepository;

    @Override
    public RequestAuthorityInfoDTO getRequestNoteInfo(final Long id) {

        final Request request = requestNoteRepository.getRequestByNoteId(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        return RequestAuthorityInfoDTO.builder()
            .authorityInfo(ResourceAuthorityInfo.builder()
                .accountId(request.getAccountId())
                .competentAuthority(request.getCompetentAuthority())
                .verificationBodyId(request.getVerificationBodyId()).build())
            .build();
    }
}
