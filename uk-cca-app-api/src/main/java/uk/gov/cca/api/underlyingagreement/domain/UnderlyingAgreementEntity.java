package uk.gov.cca.api.underlyingagreement.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "underlying_agreement")
@NamedQueries({
    @NamedQuery(
            name = UnderlyingAgreementEntity.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID,
            query = "update UnderlyingAgreementEntity u set u.fileDocumentUuid = :fileDocumentUUid where u.id = :id"
    ),
    @NamedQuery(
            name = UnderlyingAgreementEntity.NAMED_QUERY_FIND_UNDERLYING_AGREEMENT_ACCOUNT_BY_ID,
            query = "select u.accountId from UnderlyingAgreementEntity u where u.id = :id"
    )
})
public class UnderlyingAgreementEntity {
    
    public static final int CONSOLIDATION_NUMBER_DEFAULT_VALUE = 1;

    public static final String NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID = "UnderlyingAgreementEntity.updateFileDocumentUuid";
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

    @Column(name = "file_document_uuid")
    private String fileDocumentUuid;

    @Column(name = "consolidation_number")
    private int consolidationNumber;
    
    @Column(name = "activation_date")
    @NotNull
	private LocalDateTime activationDate;

    public UnderlyingAgreementEntity(UnderlyingAgreementContainer underlyingAgreementContainer, Long accountId) {
        this.underlyingAgreementContainer = underlyingAgreementContainer;
        this.accountId = accountId;
        this.consolidationNumber = CONSOLIDATION_NUMBER_DEFAULT_VALUE;
        this.activationDate = LocalDateTime.now();
    }

    @Builder
    public static UnderlyingAgreementEntity createUnderlyingAgreementEntity(UnderlyingAgreementContainer underlyingAgreementContainer, Long accountId) {
        return new UnderlyingAgreementEntity(underlyingAgreementContainer, accountId);
    }
}
