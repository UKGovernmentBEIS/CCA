package uk.gov.cca.api.authorization.ccaauth.core.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Subselect("select distinct user_id,  "
    + "case "
    + "when account_id is not null then 'OPERATOR' "
    + "when competent_authority is not null then 'REGULATOR' "
    + "when verification_body_id is not null then 'VERIFIER' "
    + "when sector_association_id is not null then 'SECTOR_USER' "
    + "end as role_type "
    + "from au_authority ")
@Immutable
@Synchronize({"au_authority"})
public class CcaUserRoleType {

    @Id
    private String userId;

    @NotNull
    private String roleType;
}
