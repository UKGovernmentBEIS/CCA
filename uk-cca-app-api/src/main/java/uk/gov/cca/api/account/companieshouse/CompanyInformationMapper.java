package uk.gov.cca.api.account.companieshouse;

import uk.gov.netz.api.common.config.MapperConfig;

@org.mapstruct.Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CompanyInformationMapper {

    CompanyProfileDTO toCompanyProfileDTO(CompanyProfile companyProfile);
}
