package uk.gov.cca.api.underlyingagreement.transform;

import java.util.Map;

import org.mapstruct.Mapper;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDetailsDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementMapper {

	UnderlyingAgreementDTO toUnderlyingAgreementDTO(UnderlyingAgreementEntity underlyingAgreementEntity);
	
    UnderlyingAgreementDetailsDTO toUnderlyingAgreementDetailsDTO(
    		Long id, Map<SchemeVersion, UnderlyingAgreementDocumentDetailsDTO> underlyingAgreementDocumentMap);

	UnderlyingAgreementDocumentDetailsDTO toUnderlyingAgreementDocumentDetailsDTO(UnderlyingAgreementDocument document, FileInfoDTO fileDocument);

}
