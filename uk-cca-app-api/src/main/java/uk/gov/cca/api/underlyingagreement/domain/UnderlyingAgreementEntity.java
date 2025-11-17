package uk.gov.cca.api.underlyingagreement.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "underlying_agreement")
@NamedQuery(
            name = UnderlyingAgreementEntity.NAMED_QUERY_FIND_UNDERLYING_AGREEMENT_ACCOUNT_BY_ID,
            query = "select u.accountId from UnderlyingAgreementEntity u where u.id = :id"
    )
public class UnderlyingAgreementEntity {
    
    public static final String NAMED_QUERY_FIND_UNDERLYING_AGREEMENT_ACCOUNT_BY_ID = "UnderlyingAgreementEntity.findUnderlyingAgreementAccountById";

    @Id
    @SequenceGenerator(name = "underlying_agreement_id_generator", sequenceName = "underlying_agreement_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "underlying_agreement_id_generator")
    private Long id;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    @NotNull
    @Valid
    private UnderlyingAgreementContainer underlyingAgreementContainer;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;
    
    @Builder.Default
    @OneToMany(mappedBy = "underlyingAgreementEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UnderlyingAgreementDocument> underlyingAgreementDocuments = new ArrayList<>();

    
    public void addUnderlyingAgreementDocument(UnderlyingAgreementDocument underlyingAgreementDocument) {
    	underlyingAgreementDocument.setUnderlyingAgreementEntity(this);
    	underlyingAgreementDocuments.add(underlyingAgreementDocument);
    }
    
    public UnderlyingAgreementDocument getDocumentForSchemeVersion(SchemeVersion schemeVersion) {
        return underlyingAgreementDocuments.stream()
				.filter(doc -> schemeVersion.equals(doc.getSchemeVersion()))
				.findFirst()
				.orElse(null);
    }
}
