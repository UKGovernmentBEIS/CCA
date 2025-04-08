package uk.gov.cca.api.underlyingagreement.validation;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_EVIDENCE_ATTACHMENT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITIES;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNIQUE_FACILITY_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Service
public class UnderlyingAgreementFacilitiesContextValidatorService extends UnderlyingAgreementSectionConstraintValidatorService<Facility> implements UnderlyingAgreementSectionContextValidator {

	private final FileAttachmentService fileAttachmentService;
	private final FacilityDataQueryService facilityDataQueryService;

    public UnderlyingAgreementFacilitiesContextValidatorService(
    		DataValidator<Facility> validator, FileAttachmentService fileAttachmentService, FacilityDataQueryService facilityDataQueryService) {
        super(validator);
        this.fileAttachmentService = fileAttachmentService;
        this.facilityDataQueryService = facilityDataQueryService;
    }

    @Override
    public BusinessValidationResult validate(final UnderlyingAgreementContainer container) {
    	Set<Facility> facilities = container.getUnderlyingAgreement().getFacilities();

        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        if (facilities.isEmpty()) {
        	violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_FACILITIES));
        }
        if(violations.isEmpty()){
			validateUniqueIds(facilities, violations);
        	facilities.forEach(section -> violations.addAll(this.validateSection(section, container)));
        }

        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }

    @Override
    protected List<UnderlyingAgreementViolation> validateSection(final Facility section, final UnderlyingAgreementContainer container) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        // Validate data
        super.validate(section).ifPresent(violation -> {
            List<Object> list = new ArrayList<>(Arrays.asList(violation.getData()));
            list.add(section.getFacilityItem().getFacilityId());
            violation.setData(list.toArray());
            violations.add(violation);
            }
        );

        // Business validations
		validateStatus(section, violations);
        validateFiles(section, violations);
        validateExistingFacilityIds(section, violations);

        return violations;
    }

	@Override
    protected String getSectionName() {
        return Facility.class.getName();
    }

	private void validateUniqueIds(Set<Facility> facilities, List<UnderlyingAgreementViolation> violations) {
		Set<String> duplicated = facilities.stream()
				.collect(Collectors.groupingBy(facility -> facility.getFacilityItem().getFacilityId()))
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.flatMap(e -> e.getValue().stream())
				.map(facility -> facility.getFacilityItem().getFacilityId())
				.collect(Collectors.toSet());

		if(!duplicated.isEmpty()) {
			violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_UNIQUE_FACILITY_ID, duplicated));
		}
	}

	private void validateStatus(final Facility section, List<UnderlyingAgreementViolation> violations) {
		String facilityId = section.getFacilityItem().getFacilityId();
		if((section.getStatus().equals(FacilityStatus.NEW) && facilityDataQueryService.isExistingFacilityId(facilityId))
				|| (List.of(FacilityStatus.LIVE, FacilityStatus.EXCLUDED).contains(section.getStatus())
					&& !facilityDataQueryService.isActiveFacility(facilityId))){
			violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_FACILITY_ID, facilityId));
		}
	}
	
	private void validateExistingFacilityIds(final Facility section, List<UnderlyingAgreementViolation> violations) {
		// Validate previousFacilityId
    	Optional.ofNullable(section.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
        		.ifPresent(id -> {
        			if (FacilityStatus.NEW.equals(section.getStatus()) && !facilityDataQueryService.isActiveFacility(id)) {
        				violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_PREVIOUS_FACILITY_ID, id));
        			}
        		});
	}
	
    private void validateFiles(final Facility section, List<UnderlyingAgreementViolation> violations) {
    	// Evidence file should be XLSX or XLS
    	Optional.ofNullable(section.getFacilityItem().getApply70Rule().getEvidenceFile())
		.ifPresent(uuid -> {
			FileDTO evidenceFile = fileAttachmentService.getFileDTO(uuid.toString());
			if (!FileType.XLSX.getMimeTypes().contains(evidenceFile.getFileType()) 
					&& !FileType.XLS.getMimeTypes().contains(evidenceFile.getFileType())) {
				violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_EVIDENCE_ATTACHMENT_TYPE));
			}
		});
    }
}
