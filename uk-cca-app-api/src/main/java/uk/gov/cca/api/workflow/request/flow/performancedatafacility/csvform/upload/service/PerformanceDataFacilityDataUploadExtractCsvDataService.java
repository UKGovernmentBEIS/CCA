package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.utils.CsvUtils;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityUploadCsvData;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadExtractCsvDataService {

    private final FileAttachmentService fileAttachmentService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;

    public Map<Long, FacilityUploadReport> exportCsvData(final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload,
                                                         TargetPeriodYear targetPeriodYear, 
                                                         List<PerformanceDataFacilityCsvErrorEntry> csvRowErrors) {
        // Extract CSV data
        List<String> errors = new ArrayList<>();
        Map<String, List<PerformanceDataFacilityUploadCsvData>> facilityCsvDataMap = new HashMap<>();
        Set<String> files = taskPayload.getReferencedAttachmentIds().stream().map(UUID::toString).collect(Collectors.toSet());
        fileAttachmentService.getFiles(files).forEach(fileDTO ->
                facilityCsvDataMap.put(fileDTO.getFileName(),
                        CsvUtils.convertToModel(fileDTO, PerformanceDataFacilityUploadCsvData.class, true, errors)));
        errors.stream().map(CsvUtils::parseCsvError).forEach(err ->
                csvRowErrors.add(PerformanceDataFacilityCsvErrorEntry.builder().filename(err.getFilename()).message(err.getMessage()).build()));

        // Validate and remove facility duplications
        removeDuplicateFacilities(facilityCsvDataMap, csvRowErrors);

        // Validate and remove facilities with product duplications
        removeFacilitiesWithProductsDuplicate(facilityCsvDataMap, csvRowErrors);
                
        Long sectorAssociationId = taskPayload.getSectorAssociationInfo().getId();
        // Get persisted facilities for this sector and scheme version
        List<FacilityDTO> persistedFacilities = facilityDataQueryService
                .getAllFacilitiesInfoDataBySectorForSchemeVersion(sectorAssociationId, SchemeVersion.CCA_3);
        Map<String, FacilityDTO> facilityByBusinessId = persistedFacilities.stream()
        	    .collect(Collectors.toMap(FacilityDTO::getFacilityBusinessId, Function.identity()));
        
        // Find which provided facilities are locked for this target year
        Set<Long> lockedFacilityIds = getLockedCsvFacilityIds(targetPeriodYear, facilityCsvDataMap, facilityByBusinessId);
        
        // Build facility upload reports
        Map<Long, FacilityUploadReport> facilityReports = new HashMap<>();
        facilityCsvDataMap.forEach((filename, list) -> list.forEach(data -> {
            FacilityDTO facility = facilityByBusinessId.get(data.getFacilityBusinessId());
            
            // Facility does not exist
            if (facility == null) {
            	addCsvError(csvRowErrors, data.getFacilityBusinessId(), filename, 
            			PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_CSV_FACILITY_BUSINESS_ID.getMessage());
            } 
            // Facility is locked
            else if (lockedFacilityIds.contains(facility.getId())) {
            	addCsvError(csvRowErrors, data.getFacilityBusinessId(), filename, 
            			PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_IS_LOCKED.getMessage());
            } else {
            	facilityReports.put(
            			facility.getId(),
            			FacilityUploadReport.builder()
            				.facilityId(facility.getId())
            				.facilityBusinessId(facility.getFacilityBusinessId())
            				.accountId(facility.getAccountId())
            				.csvFileName(filename)
            				.csvData(data)
            				.build());
            }
        }));

        return facilityReports;
    }

	private void removeDuplicateFacilities(Map<String, List<PerformanceDataFacilityUploadCsvData>> facilityCsvDataMap,
                                           List<PerformanceDataFacilityCsvErrorEntry> csvRowErrors) {
        Map<String, Long> counts = facilityCsvDataMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(PerformanceDataFacilityUploadCsvData::getFacilityBusinessId, Collectors.counting()));

        counts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .forEach(facilityBusinessId ->
                        // Remove duplications
                        facilityCsvDataMap.forEach((filename, list) -> {
                            boolean removed = list.removeIf(data -> facilityBusinessId.equals(data.getFacilityBusinessId()));
                            if (removed) {
                                csvRowErrors.add(PerformanceDataFacilityCsvErrorEntry.builder()
                                        .facilityBusinessId(facilityBusinessId)
                                        .filename(filename)
                                        .message(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_CSV_FACILITY_DUPLICATE_FOUND.getMessage())
                                        .build());
                            }
                        }));
    }

    private void removeFacilitiesWithProductsDuplicate(Map<String, List<PerformanceDataFacilityUploadCsvData>> facilityCsvDataMap,
                                                       List<PerformanceDataFacilityCsvErrorEntry> csvRowErrors) {
        Map<String, String> facilitiesToBeRemoved = new HashMap<>();

        facilityCsvDataMap.forEach((filename, list) ->
                list.forEach(data -> {
                    List<String> productNames = data.getProductNames();
                    Set<String> duplicateProducts = productNames.stream()
                            .filter(name -> Collections.frequency(productNames, name) > 1)
                            .collect(Collectors.toSet());
                    if(!duplicateProducts.isEmpty()) {
                        facilitiesToBeRemoved.put(filename, data.getFacilityBusinessId());
                    }
                }));

        facilitiesToBeRemoved.forEach((filenameToBeRemoved, facilityBusinessIdToBeRemoved) ->
                // Remove duplications
                facilityCsvDataMap.forEach((filename, list) -> {
                    boolean removed = list.removeIf(data -> facilityBusinessIdToBeRemoved.equals(data.getFacilityBusinessId()));
                    if (removed) {
                        csvRowErrors.add(PerformanceDataFacilityCsvErrorEntry.builder()
                                .facilityBusinessId(facilityBusinessIdToBeRemoved)
                                .filename(filename)
                                .message(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_CSV_FACILITY_DUPLICATE_PRODUCTS_FOUND.getMessage())
                                .build());
                    }
                }));
    }
    
    private Set<Long> getLockedCsvFacilityIds(TargetPeriodYear targetPeriodYear,
			Map<String, List<PerformanceDataFacilityUploadCsvData>> facilityCsvDataMap,
			Map<String, FacilityDTO> persistedFacilityByBusinessId) {
		
		Set<String> uploadedBusinessIds = facilityCsvDataMap.values().stream()
                .flatMap(List::stream)
                .map(PerformanceDataFacilityUploadCsvData::getFacilityBusinessId)
                .collect(Collectors.toSet());
		Set<Long> uploadedFacilityIds = uploadedBusinessIds.stream()
			    .map(persistedFacilityByBusinessId::get)
			    .filter(Objects::nonNull)
			    .map(FacilityDTO::getId)
			    .collect(Collectors.toSet());

        return performanceDataFacilityStatusQueryService.getLockedFacilityIds(uploadedFacilityIds, targetPeriodYear.getTargetYear());
	}
    
    private void addCsvError(
            List<PerformanceDataFacilityCsvErrorEntry> errors,
            String facilityBusinessId,
            String filename,
            String message) {

        errors.add(
            PerformanceDataFacilityCsvErrorEntry.builder()
                .facilityBusinessId(facilityBusinessId)
                .filename(filename)
                .message(message)
                .build()
        );
    }
}
