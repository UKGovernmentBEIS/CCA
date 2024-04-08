package uk.gov.cca.api.verificationbody.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.verificationbody.domain.VerificationBody;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.cca.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface VerificationBodyMapper {

    VerificationBodyDTO toVerificationBodyDTO(VerificationBody verificationBody);

    VerificationBodyInfoDTO toVerificationBodyInfoDTO(VerificationBody verificationBody);

    List<VerificationBodyInfoDTO> toVerificationBodyInfoDTO(List<VerificationBody> verificationBodies);
    
    VerificationBodyNameInfoDTO toVerificationBodyNameInfoDTO(VerificationBody verificationBody);

    VerificationBody toVerificationBody(VerificationBodyEditDTO verificationBodyEditDTO);

    VerificationBodyDetails toVerificationBodyDetails(VerificationBodyDTO verificationBodyDTO);
}
