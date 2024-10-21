package uk.gov.cca.api.migration.sectoruser;

import java.util.List;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.migration.MigrationEndpoint;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class SectorUserInvitationMapper {
    
    public SectorUserInvitationVO toSectorUserInvitation(String[] values, List<String> failedEntries) {
        Long rowId = Long.valueOf(values[0].trim());
        if(values.length != 8) {
            failedEntries.add(SectorUserInvitationHelper.constructErrorMessage(rowId, "Input data not in expected format"));
            return null;
        }

        SectorUserInvitationVO sectorUserInvitation = null;
        try {
            sectorUserInvitation = SectorUserInvitationVO.builder()
                .rowId(rowId)
                .sectorAcronym(values[1].trim())
                .inviterEmail(values[2].trim())
                .roleCode(values[3].trim())
                .firstName(values[4].trim())
                .lastName(values[5].trim())
                .email(values[6].trim())
                .contactType(ContactType.valueOf(values[7].trim()))
                .build();
        } catch (Exception ex) {
            failedEntries.add(SectorUserInvitationHelper.constructErrorMessage(rowId, "Input data not in expected format"));
        }
        
        return sectorUserInvitation;
    }

}
