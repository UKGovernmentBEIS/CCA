package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountPayload;

@Mapper(componentModel = "spring",
        config = MapperConfig.class,
        imports = {java.time.LocalDateTime.class, TargetUnitAccountStatus.class, FinancialIndependenceStatus.class}
)
public interface TargetUnitAccountPayloadMapper {

    @Mapping(target = "createdBy", source = "creator")
    @Mapping(target = "creationDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(TargetUnitAccountStatus.NEW)")
    @Mapping(target = "financialIndependenceStatus", expression = "java(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)")
    TargetUnitAccountDTO toTargetUnitAccountDTO(TargetUnitAccountPayload accountPayload, Long sectorAssociationId, String creator);

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD)")
    @Mapping(target = "businessId", source = "businessId")
    @Mapping(target = "sectorAssociationId", source = "sectorAssociationId")
    TargetUnitAccountCreationRequestPayload toTargetUnitAccountCreationRequestPayload(TargetUnitAccountPayload payload, String businessId, Long sectorAssociationId);

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "businessId", source = "businessId")
    TargetUnitAccountCreationSubmittedRequestActionPayload toTargetUnitAccountCreationSubmittedRequestActionPayload(TargetUnitAccountPayload payload, String businessId);

}
