package uk.gov.cca.api.authorization.ccaauth.core.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    public Set<Long> getUserSectorAssociations(AppUser user) {
        return user.getAuthorities().stream()
            .map(AppCcaAuthority.class::cast)
            .map(AppCcaAuthority::getSectorAssociationId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    }
}
