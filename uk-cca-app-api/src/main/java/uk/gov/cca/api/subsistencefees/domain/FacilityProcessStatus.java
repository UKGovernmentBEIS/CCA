package uk.gov.cca.api.subsistencefees.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.common.config.YearAttributeConverter;

import java.time.Year;

@NamedQuery(
        name = FacilityProcessStatus.NAMED_QUERY_FIND_ACCOUNTS_FOR_SUBSISTENCE_FEES_RUN,
        query = "select distinct fd.accountId " +
                "from FacilityData fd inner join TargetUnitAccount acc on fd.accountId = acc.id " +
                "where acc.financialIndependenceStatus = 'FINANCIALLY_INDEPENDENT' " +
                "  and coalesce(fd.chargeStartDate, fd.createdDate) <= :endDateOfChargingYear " +
                "  and (fd.schemeExitDate is null or fd.schemeExitDate >= :firstDateOfChargingYear) " +
                "  and not exists(select 1 from FacilityProcessStatus fps " +
                "                 where fd.id = fps.facilityId " +
                "                   and fps.chargingYear = :chargingYear " +
                "                   and fps.moaType = 'TARGET_UNIT_MOA' )"
)
@NamedQuery(
        name = FacilityProcessStatus.NAMED_QUERY_GET_SECTOR_FACILITIES_FOR_SUBSISTENCE_FEES_RUN,
        query = "select new uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO(fd.id, fd.facilityId as facilityId, fd.siteName as siteName, acc.businessId as businessId, acc.name as operatorName, acc.id as accountId) " +
                "from FacilityData fd inner join TargetUnitAccount acc on fd.accountId = acc.id " +
                "where acc.sectorAssociationId = :sectorAssociationId " +
                "  and acc.financialIndependenceStatus = 'NON_FINANCIALLY_INDEPENDENT' " +
                "  and coalesce(fd.chargeStartDate, fd.createdDate) <= :endDateOfChargingYear " +
                "  and (fd.schemeExitDate is null or fd.schemeExitDate >= :firstDateOfChargingYear) " +
                "  and not exists(select 1 from FacilityProcessStatus fps " +
                "                 where fd.id = fps.facilityId " +
                "                   and fps.chargingYear = :chargingYear " +
                "                   and fps.moaType = 'SECTOR_MOA' )"
)
@NamedQuery(
        name = FacilityProcessStatus.NAMED_QUERY_GET_ACCOUNT_FACILITIES_FOR_SUBSISTENCE_FEES_RUN,
        query = "select new uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO(fd.id, fd.facilityId as facilityId, fd.siteName as siteName, acc.businessId as businessId, acc.name as operatorName, acc.id as accountId) " +
                "from FacilityData fd inner join TargetUnitAccount acc on fd.accountId = acc.id " +
                "where acc.id = :accountId " +
                "  and acc.financialIndependenceStatus = 'FINANCIALLY_INDEPENDENT' " +
                "  and coalesce(fd.chargeStartDate, fd.createdDate) <= :endDateOfChargingYear " +
                "  and (fd.schemeExitDate is null or fd.schemeExitDate >= :firstDateOfChargingYear) " +
                "  and not exists(select 1 from FacilityProcessStatus fps " +
                "                 where fd.id = fps.facilityId " +
                "                   and fps.chargingYear = :chargingYear " +
                "                   and fps.moaType = 'TARGET_UNIT_MOA' )"
)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sfr_facility_process_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"facility_id", "charging_year", "moa_type"}))
public class FacilityProcessStatus {

    public static final String NAMED_QUERY_FIND_ACCOUNTS_FOR_SUBSISTENCE_FEES_RUN = "FacilityProcessDataStatus.findTargetUnitAccountsForSubsistenceFeesRun";
    public static final String NAMED_QUERY_GET_SECTOR_FACILITIES_FOR_SUBSISTENCE_FEES_RUN = "FacilityProcessDataStatus.findSectorFacilitiesForSubsistenceFeesRun";
    public static final String NAMED_QUERY_GET_ACCOUNT_FACILITIES_FOR_SUBSISTENCE_FEES_RUN = "FacilityProcessDataStatus.findAccountFacilitiesForSubsistenceFeesRun";

    @Id
    @SequenceGenerator(name = "sfr_facility_process_status_id_generator", sequenceName = "sfr_facility_process_status_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sfr_facility_process_status_id_generator")
    private Long id;

    @Column(name = "facility_id")
    @NotNull
    private Long facilityId;

    @Convert(converter = YearAttributeConverter.class)
    @Column(name = "charging_year")
    @NotNull
    private Year chargingYear;

    @Column(name = "run_id")
    @NotNull
    private Long runId;

    @Enumerated(EnumType.STRING)
    @Column(name = "moa_type")
    @NotNull
    private MoaType moaType;
}
