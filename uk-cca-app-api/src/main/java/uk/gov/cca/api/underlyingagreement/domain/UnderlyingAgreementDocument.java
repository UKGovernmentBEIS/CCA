package uk.gov.cca.api.underlyingagreement.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "underlying_agreement_document")
@NamedQuery(
        name = UnderlyingAgreementDocument.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID,
        query = "update UnderlyingAgreementDocument u set u.fileDocumentUuid = :fileDocumentUUid where u.id = :id"
)
public class UnderlyingAgreementDocument {

	public static final String NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID = "UnderlyingAgreementDocument.updateFileDocumentUuid";
	public static final int CONSOLIDATION_NUMBER_DEFAULT_VALUE = 1;
	
	@Id
    @SequenceGenerator(name = "underlying_agreement_document_id_generator", sequenceName = "underlying_agreement_document_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "underlying_agreement_document_id_generator")
    private Long id;

	@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "underlying_agreement_id")
	private UnderlyingAgreementEntity underlyingAgreementEntity;
	
    @Column(name = "file_document_uuid")
    private String fileDocumentUuid;

    @Column(name = "consolidation_number")
    @NotNull
    private int consolidationNumber;
    
    @Column(name = "activation_date")
    @NotNull
	private LocalDateTime activationDate;

    @Column(name = "terminated_date")
    private LocalDateTime terminatedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_version")
    @NotNull
    private SchemeVersion schemeVersion;
    
    public UnderlyingAgreementDocument(SchemeVersion schemeVersion) {
        this.consolidationNumber = CONSOLIDATION_NUMBER_DEFAULT_VALUE;
        this.activationDate = LocalDateTime.now();
        this.schemeVersion = schemeVersion;
    }

    @Builder
    public static UnderlyingAgreementDocument createUnderlyingAgreementDocument(SchemeVersion schemeVersion) {
        return new UnderlyingAgreementDocument(schemeVersion);
    }
}
