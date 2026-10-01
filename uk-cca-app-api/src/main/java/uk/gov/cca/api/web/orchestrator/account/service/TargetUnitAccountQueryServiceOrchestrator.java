package uk.gov.cca.api.web.orchestrator.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.CcaAccountSearchResultInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSearchCriteria;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.web.orchestrator.account.dto.TargetUnitAccountDetailsResponseDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.search.criteria.AccountSearchCommonSortField;
import uk.gov.netz.api.account.search.criteria.AccountSearchContactFilter;
import uk.gov.netz.api.account.search.criteria.AccountSearchFilterCriteria;
import uk.gov.netz.api.account.search.service.AccountSearchQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.util.HashSet;
import java.util.List;

import static uk.gov.cca.api.account.domain.TargetUnitAccountStatus.LIVE;
import static uk.gov.cca.api.account.domain.TargetUnitAccountStatus.TERMINATED;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountQueryServiceOrchestrator {

    private final TargetUnitAccountService targetUnitAccountService;
    private final AccountSearchQueryService<TargetUnitAccount, CcaAccountSearchResultInfoDTO> ccaAccountSearchQueryService;
    private final SubsectorAssociationService subsectorAssociationService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;

    public TargetUnitAccountDetailsResponseDTO getTargetUnitAccountDetailsById(Long accountId) {
        TargetUnitAccountDetailsDTO targetUnitAccountDetails = targetUnitAccountService
                .getTargetUnitAccountDetailsById(accountId);

        SubsectorAssociationDTO subsectorAssociation = new SubsectorAssociationDTO();
        if (!ObjectUtils.isEmpty(targetUnitAccountDetails.getSubsectorAssociationId())) {
            subsectorAssociation = subsectorAssociationService
                    .getSubsectorById(targetUnitAccountDetails.getSubsectorAssociationId());
        }

        TargetUnitAccountDetailsResponseDTO responseDTO = TargetUnitAccountDetailsResponseDTO.builder()
                .targetUnitAccountDetails(targetUnitAccountDetails)
                .subsectorAssociation(subsectorAssociation)
                .build();

        if (targetUnitAccountDetails.getStatus() != null
                && List.of(LIVE, TERMINATED).contains(targetUnitAccountDetails.getStatus())) {
            responseDTO.setUnderlyingAgreementDetails(
                    underlyingAgreementQueryService.getUnderlyingAgreementDetailsByAccountId(accountId));
        }

        return responseDTO;
    }

    public AccountSearchResults<CcaAccountSearchResultInfoDTO> searchUserAccounts(final AppUser appUser, final TargetUnitAccountSearchCriteria searchCriteria) {
        AccountSearchFilterCriteria criteria = AccountSearchFilterCriteria.builder()
                .term(searchCriteria.getTerm())
                .paging(PagingRequest.builder()
                        .pageNumber(searchCriteria.getPage())
                        .pageSize(searchCriteria.getSize())
                        .build())
                .statuses(new HashSet<>())
                .sortField(AccountSearchCommonSortField.OPERATOR_NAME)
                .sortDirection(Sort.Direction.ASC)
                .build();

        return ccaAccountSearchQueryService.search(appUser, criteria, AccountSearchContactFilter.none());
    }
}
