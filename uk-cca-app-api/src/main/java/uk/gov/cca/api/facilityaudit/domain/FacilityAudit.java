package uk.gov.cca.api.facilityaudit.domain;


import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "facility_audit")
public class FacilityAudit {

	@Id
	@SequenceGenerator(name = "facility_audit_id_generator", sequenceName = "facility_audit_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facility_audit_id_generator")
	@Column(name = "id")
	private Long id;

	@NotNull
	@EqualsAndHashCode.Include()
	@Column(name = "facility_id", unique = true)
	private Long facilityId;

	@Column(name = "audit_required", nullable = false)
	private boolean auditRequired;

	@Type(JsonType.class)
	@Builder.Default
	@Column(name = "reasons", columnDefinition = "jsonb")
	private List<FacilityAuditReasonType> reasons = new ArrayList<>();

	@Column(name = "comments", columnDefinition = "text")
	@Size(max = 10000)
	private String comments;

	@Column(name = "updated_by")
	@Size(max = 255)
	private String updatedBy;

	@Column(name = "last_updated_on")
	@UpdateTimestamp
	private LocalDateTime lastUpdatedOn;

}
