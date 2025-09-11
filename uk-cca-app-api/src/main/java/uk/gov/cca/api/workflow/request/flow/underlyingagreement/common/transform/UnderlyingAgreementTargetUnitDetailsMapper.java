package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {NoticeRecipientType.class})
public interface UnderlyingAgreementTargetUnitDetailsMapper {

    @Mapping(target = "operatorAddress", source = "accountDetails.address")
    @Mapping(target = "responsiblePersonDetails", source = "accountDetails.responsiblePerson")
    @Mapping(target = "isCompanyRegistrationNumber", expression = "java(org.apache.commons.lang3.ObjectUtils.isEmpty(accountDetails.getCompanyRegistrationNumber()) ? Boolean.FALSE : Boolean.TRUE)")
    UnderlyingAgreementTargetUnitDetails toUnderlyingAgreementTargetUnitDetails(TargetUnitAccountDetails accountDetails, String subsectorAssociationName);

    @Mapping(target = "type", expression = "java(NoticeRecipientType.RESPONSIBLE_PERSON)")
    NoticeRecipientDTO toResponsiblePersonNoticeRecipientDTO(UnderlyingAgreementTargetUnitResponsiblePerson responsiblePerson);
}
