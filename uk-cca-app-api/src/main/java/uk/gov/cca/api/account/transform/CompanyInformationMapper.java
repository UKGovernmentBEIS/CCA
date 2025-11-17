package uk.gov.cca.api.account.transform;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uk.gov.cca.api.account.domain.dto.CompanyProfileInfo;
import uk.gov.cca.api.account.domain.dto.CompanyProfileDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.companieshouse.SicCode;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CompanyInformationMapper {

	@Mapping(target = "sicCodes", source = "sicCodes", qualifiedByName = "sicCodes")
	@Mapping(target = "operatorType", source = "companyType.description")
	CompanyProfileDTO toCompanyProfileDTO (CompanyProfileInfo companyProfile);
	
	@Named("sicCodes")
    default List<String> mapSicCodes(List<SicCode> sicCodes) {
		if (sicCodes == null) {
			return List.of();
		}
        return sicCodes.stream().map(SicCode::getCode).toList();
    }
}
