package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "tpr_buy_out_surplus_processed_data",
		uniqueConstraints = @UniqueConstraint(columnNames = {"performance_data_id", "performance_data_resource_type"}) )
@NamedQuery(
        name = BuyOutSurplusProcessedData.NAMED_QUERY_FIND_LATEST_BUY_OUT_SURPLUS_PROCESSED_FACILITIES_PRIMARY_PERFORMANCE_DATA_BY_FACILITY_ID_IN_AND_TARGET_PERIOD,
        query = "SELECT new uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo(pdf.facilityId, bs.performanceDataId) " +
                "FROM PerformanceDataFacilityEntity pdf " +
                "JOIN BuyOutSurplusProcessedData bs ON bs.performanceDataId  = pdf.id " +
                "WHERE pdf.facilityId IN (:facilityIds) " +
                "AND bs.performanceDataResourceType = PerformanceDataResourceType.FACILITY " +
                "AND pdf.targetPeriod.businessId = :targetPeriodType " +
                "AND pdf.submissionType = PerformanceDataSubmissionType.PRIMARY " +
                "AND pdf.reportVersion  = ( " +
                "      SELECT MAX(pdf2.reportVersion) " +
                "      FROM PerformanceDataFacilityEntity pdf2 " +
                "      WHERE pdf2.facilityId = pdf.facilityId " +
                "        AND pdf2.targetPeriod = pdf.targetPeriod " +
                "        AND pdf2.submissionType = pdf.submissionType " +
                ")"
)
public class BuyOutSurplusProcessedData {

    public static final String NAMED_QUERY_FIND_LATEST_BUY_OUT_SURPLUS_PROCESSED_FACILITIES_PRIMARY_PERFORMANCE_DATA_BY_FACILITY_ID_IN_AND_TARGET_PERIOD =
            "BuyOutSurplusProcessedData.findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType";

    @Id
    @SequenceGenerator(name = "buy_out_surplus_processed_data_id_generator", sequenceName = "tpr_buy_out_surplus_processed_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "buy_out_surplus_processed_data_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "performance_data_id", updatable = false)
    private Long performanceDataId;

    @NotNull
    @CreatedDate
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;
    
    @EqualsAndHashCode.Include()
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "performance_data_resource_type", updatable = false)
    private PerformanceDataResourceType performanceDataResourceType;
}
