package uk.gov.cca.api.token;

import lombok.experimental.UtilityClass;
import uk.gov.netz.api.token.JwtTokenAction;

@UtilityClass
public class CcaJwtTokenAction {

    public static final JwtTokenAction SECTOR_USER_INVITATION = new JwtTokenAction("sector_user_invitation", "authority_uuid");

}
