package uk.gov.cca.api.subsistencefees.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesRunAuthorityInfoProvider;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo;
import uk.gov.cca.api.subsistencefees.repository.FacilityProcessStatusRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesRunRepository;
import uk.gov.cca.api.subsistencefees.transform.SubsistenceFeesMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunQueryService implements SubsistenceFeesRunAuthorityInfoProvider {

    private final FacilityProcessStatusRepository facilityProcessStatusRepository;
    private final SubsistenceFeesRunRepository subsistenceFeesRunRepository;
    private final SubsistenceFeesMapper SUBSISTENCE_FEES_MAPPER = Mappers.getMapper(SubsistenceFeesMapper.class);

    private static final MonthDay startMonthDay = MonthDay.of(1, 1);
    private static final MonthDay endMonthDay = MonthDay.of(12, 31);

    public SubsistenceFeesRunSearchResults getSubsistenceFeesRuns(AppUser appUser, PagingRequest pagingRequest) {
        final Pageable pageable = getPageable(pagingRequest);

        // Find submitted runs for the requested page
        Page<SubsistenceFeesRun> results = subsistenceFeesRunRepository
                .findSubsistenceFeesRunsByCompetentAuthorityAndSubmissionDateNotNull(pageable, appUser.getCompetentAuthority());

        if (ObjectUtils.isEmpty(results)) {
            return SubsistenceFeesRunSearchResults.emptySubsistenceFeesRunsSearchResults();
        }

        // For the runs in the current page, get the details and calculated amounts
        Set<Long> runIds = results.stream().map(SubsistenceFeesRun::getId).collect(Collectors.toSet());
        List<SubsistenceFeesRunSearchResultInfoDTO> runs = subsistenceFeesRunRepository.findSubsistenceFeesRunsWithAmountsByIds(runIds)
                .stream()
                .map(SUBSISTENCE_FEES_MAPPER::toSubsistenceFeesRunSearchResultInfoDTO)
                .collect(Collectors.toList());

        return SubsistenceFeesRunSearchResults.builder()
                .subsistenceFeesRuns(runs)
                .total(results.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public SubsistenceFeesRunDetailsDTO getSubsistenceFeesRunDetailsById(Long runId) {
        SubsistenceFeesRunDetailsInfo runDetailsInfo = subsistenceFeesRunRepository.findSubsistenceFeesRunDetailsById(runId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        SubsistenceFeesRunMoaDetailsInfo runMoasDetailsInfo = subsistenceFeesRunRepository.findSubsistenceFeesRunMoaDetailsById(runId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        return SUBSISTENCE_FEES_MAPPER.toSubsistenceFeesRunDetailsDTO(runDetailsInfo, runMoasDetailsInfo);
    }


    private Pageable getPageable(PagingRequest pagingRequest) {
        return PageRequest.of(
                pagingRequest.getPageNumber().intValue(),
                pagingRequest.getPageSize().intValue(),
                Sort.by(Direction.DESC, "submissionDate"));
    }

    public Set<Long> getTargetAccountIdsForSubsistenceFeesRun(Year chargingYear) {
        final LocalDate firstDateOfChargingYear = chargingYear.atMonthDay(startMonthDay);
        final LocalDate endDateOfChargingYear = chargingYear.atMonthDay(endMonthDay);
        return facilityProcessStatusRepository.findTargetUnitAccountsForSubsistenceFeesRun(chargingYear, firstDateOfChargingYear, endDateOfChargingYear);
    }

    public boolean isSectorEligibleForSubsistenceFeesRun(long sectorAssociationId, Year chargingYear) {
        final LocalDate firstDateOfChargingYear = chargingYear.atMonthDay(startMonthDay);
        final LocalDate endDateOfChargingYear = chargingYear.atMonthDay(endMonthDay);
        return !facilityProcessStatusRepository
                .findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear)
                .isEmpty();
    }

    public List<EligibleFacilityDTO> getSectorEligibleFacilitiesForSubsistenceFeesRun(long sectorAssociationId, Year chargingYear) {
        final LocalDate firstDateOfChargingYear = chargingYear.atMonthDay(startMonthDay);
        final LocalDate endDateOfChargingYear = chargingYear.atMonthDay(endMonthDay);
        return facilityProcessStatusRepository
                .findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);
    }

    public List<EligibleFacilityDTO> getAccountEligibleFacilitiesForSubsistenceFeesRun(long accountId, Year chargingYear) {
        final LocalDate firstDateOfChargingYear = chargingYear.atMonthDay(startMonthDay);
        final LocalDate endDateOfChargingYear = chargingYear.atMonthDay(endMonthDay);
        return facilityProcessStatusRepository
                .findAccountFacilitiesForSubsistenceFeesRun(accountId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);
    }

    @Override
    public CompetentAuthorityEnum getSubsistenceFeesRunCaById(Long runId) {
        return getSubsistenceFeesRunById(runId).getCompetentAuthority();
    }

    SubsistenceFeesRun getSubsistenceFeesRunById(Long runId) {
        return subsistenceFeesRunRepository.findById(runId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
