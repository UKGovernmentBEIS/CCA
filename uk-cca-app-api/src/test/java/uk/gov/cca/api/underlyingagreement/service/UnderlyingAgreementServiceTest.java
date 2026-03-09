package uk.gov.cca.api.underlyingagreement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementDocumentRepository;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementServiceTest {

    @InjectMocks
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private UnderlyingAgreementRepository underlyingAgreementRepository;

    @Mock
    private UnderlyingAgreementDocumentRepository underlyingAgreementDocumentRepository;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    
    @Mock
    private UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Test
    void submitUnderlyingAgreement() {
        final long accountId = 1L;
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .facilities(Set.of(Facility.builder()
                        .facilityItem(FacilityItem.builder()
                                .facilityId("fid")
                                .facilityDetails(FacilityDetails.builder()
                                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
                                        .build())
                                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                        .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                                .hasVariableEnergy(true)
                                                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                                                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                                                        .baselineYear(Year.of(2022))
                                                        .productName("Product1")
                                                        .productStatus(ProductStatus.NEW)
                                                        .energy(BigDecimal.valueOf(256))
                                                        .throughput(BigDecimal.valueOf(120))
                                                        .throughputUnit("Each")
                                                        .build()))
                                                .build())
                                        .build())
                                .build())
                        .status(FacilityStatus.NEW)
                        .build()))
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .build();

        // update facility status to 'LIVE' before save
        activateFacilities(container);

        // update variable energy product status to 'LIVE' before save
        activateVariableEnergyProducts(container);

        UnderlyingAgreementDocument unaDocumentCca2 = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_2);
        UnderlyingAgreementDocument unaDocumentCca3 = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_3);
        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.builder()
                .accountId(accountId)
                .underlyingAgreementContainer(container)
                .build();
        entity.addUnderlyingAgreementDocument(unaDocumentCca2);
        entity.addUnderlyingAgreementDocument(unaDocumentCca3);
        
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		container.getUnderlyingAgreement().getFacilities()))
				.thenReturn(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));

        // Invoke
        underlyingAgreementService.submitUnderlyingAgreement(container, accountId, underlyingAgreementValidationContext);

        ArgumentCaptor<UnderlyingAgreementEntity> captor =
                ArgumentCaptor.forClass(UnderlyingAgreementEntity.class);
        // Verify
        verify(underlyingAgreementValidatorService, times(1)).validate(container, underlyingAgreementValidationContext);
        verify(underlyingAgreementRepository, times(1)).save(captor.capture());
        verify(underlyingAgreementSchemeVersionsHelperService, times(1))
        		.calculateSchemeVersionsFromActiveFacilities(container.getUnderlyingAgreement().getFacilities());

        assertTrue(container.getUnderlyingAgreement().getFacilities().stream()
                .allMatch(f -> f.getStatus().equals(FacilityStatus.LIVE)));
        assertTrue(container.getUnderlyingAgreement().getFacilities().stream()
                .flatMap(f -> f.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption().getVariableEnergyConsumptionDataByProduct().stream())
                .allMatch(p -> p.getProductStatus().equals(ProductStatus.LIVE)));
        assertThat(captor.getValue().getUnderlyingAgreementContainer()).isEqualTo(container);
        assertThat(captor.getValue().getUnderlyingAgreementDocuments()).hasSize(2);
        assertThat(captor.getValue().getUnderlyingAgreementDocuments().getFirst().getActivationDate()).isNotNull();
        assertThat(captor.getValue().getUnderlyingAgreementDocuments().getLast().getActivationDate()).isNotNull();
    }

    @Test
    void updateUnderlyingAgreement_no_document() {
        final long accountId = 1L;
        final UnderlyingAgreementContainer newContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder()
                                        .status(FacilityStatus.NEW)
                                        .facilityItem(FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                        .build())
                                                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                                        .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                                                .variableEnergyConsumptionDataByProduct(List.of(
                                                                        ProductVariableEnergyConsumptionData.builder()
                                                                                .productStatus(ProductStatus.NEW)
                                                                                .build()
                                                                ))
                                                                .build())
                                                        .build())
                                                .build())
                                        .build()
                        ))
                        .build())
                .build();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
        List<UnderlyingAgreementDocument> underlyingAgreementDocuments = List.of(
                UnderlyingAgreementDocument.builder().consolidationNumber(2).schemeVersion(SchemeVersion.CCA_2).build(),
                UnderlyingAgreementDocument.builder().consolidationNumber(3).schemeVersion(SchemeVersion.CCA_3).build()
        );
        UnderlyingAgreementEntity persistentEntity = UnderlyingAgreementEntity.builder()
                .underlyingAgreementDocuments(new ArrayList<>(underlyingAgreementDocuments))
                .build();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(persistentEntity));
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		newContainer.getUnderlyingAgreement().getFacilities()))
				.thenReturn(Set.of(SchemeVersion.CCA_3));

        // Invoke
        underlyingAgreementService.updateUnderlyingAgreement(newContainer, accountId, underlyingAgreementValidationContext, false);

        // Verify
        assertThat(persistentEntity.getUnderlyingAgreementContainer()).isEqualTo(newContainer);
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().stream()
                .allMatch(f -> f.getStatus().equals(FacilityStatus.LIVE))).isTrue();
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().stream()
                .allMatch(f -> f.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption().getVariableEnergyConsumptionDataByProduct()
                        .stream().allMatch(p -> p.getProductStatus().equals(ProductStatus.LIVE)))).isTrue();
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_2).getConsolidationNumber()).isEqualTo(2);
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_2).getTerminatedDate()).isNotNull();
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_3).getConsolidationNumber()).isEqualTo(3);
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_3).getTerminatedDate()).isNull();
        verify(underlyingAgreementValidatorService, times(1)).validate(newContainer, underlyingAgreementValidationContext);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1))
				.calculateSchemeVersionsFromActiveFacilities(newContainer.getUnderlyingAgreement().getFacilities());
    }

    @Test
    void updateUnderlyingAgreement_with_update_and_terminate_document() {
        final long accountId = 1L;
        final UnderlyingAgreementContainer newContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder()
                                        .status(FacilityStatus.NEW)
                                        .facilityItem(FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                        .build())
                                                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                                        .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                                                .variableEnergyConsumptionDataByProduct(List.of(
                                                                        ProductVariableEnergyConsumptionData.builder()
                                                                                .productStatus(ProductStatus.NEW)
                                                                                .build()
                                                                ))
                                                                .build())
                                                        .build())
                                                .build())
                                        .build()
                        ))
                        .build())
                .build();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
        List<UnderlyingAgreementDocument> underlyingAgreementDocuments = List.of(
                UnderlyingAgreementDocument.builder().consolidationNumber(2).schemeVersion(SchemeVersion.CCA_2).build(),
                UnderlyingAgreementDocument.builder().consolidationNumber(3).schemeVersion(SchemeVersion.CCA_3).build()
        );
        UnderlyingAgreementEntity persistentEntity = UnderlyingAgreementEntity.builder()
                .underlyingAgreementDocuments(new ArrayList<>(underlyingAgreementDocuments))
                .build();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(persistentEntity));
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		newContainer.getUnderlyingAgreement().getFacilities()))
				.thenReturn(Set.of(SchemeVersion.CCA_3));

        // Invoke
        underlyingAgreementService
                .updateUnderlyingAgreement(newContainer, accountId, underlyingAgreementValidationContext, true);

        // Verify
        assertThat(persistentEntity.getUnderlyingAgreementContainer()).isEqualTo(newContainer);
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().stream()
                .allMatch(f -> f.getStatus().equals(FacilityStatus.LIVE))).isTrue();
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().stream()
                .allMatch(f -> f.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption().getVariableEnergyConsumptionDataByProduct()
                        .stream().allMatch(p -> p.getProductStatus().equals(ProductStatus.LIVE)))).isTrue();
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_2).getConsolidationNumber()).isEqualTo(2);
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_2).getTerminatedDate()).isNotNull();
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_3).getConsolidationNumber()).isEqualTo(4);
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_3).getTerminatedDate()).isNull();

        verify(underlyingAgreementValidatorService, times(1)).validate(newContainer, underlyingAgreementValidationContext);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1))
				.calculateSchemeVersionsFromActiveFacilities(newContainer.getUnderlyingAgreement().getFacilities());
    }

    @Test
    void updateUnderlyingAgreement_with_update_and_new_document() {
        final long accountId = 1L;
        final UnderlyingAgreementContainer newContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder()
                                        .status(FacilityStatus.NEW)
                                        .facilityItem(FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
                                                        .build())
                                                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                                        .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                                                .variableEnergyConsumptionDataByProduct(List.of(
                                                                        ProductVariableEnergyConsumptionData.builder()
                                                                                .productStatus(ProductStatus.NEW)
                                                                                .build()
                                                                ))
                                                                .build())
                                                        .build())
                                                .build())
                                        .build()
                        ))
                        .build())
                .build();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
        List<UnderlyingAgreementDocument> underlyingAgreementDocuments = List.of(
                UnderlyingAgreementDocument.builder().consolidationNumber(2).schemeVersion(SchemeVersion.CCA_2).build()
        );
        UnderlyingAgreementEntity persistentEntity = UnderlyingAgreementEntity.builder()
                .underlyingAgreementDocuments(new ArrayList<>(underlyingAgreementDocuments))
                .build();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(persistentEntity));
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		newContainer.getUnderlyingAgreement().getFacilities()))
				.thenReturn(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));

        // Invoke
        underlyingAgreementService
                .updateUnderlyingAgreement(newContainer, accountId, underlyingAgreementValidationContext, true);

        // Verify
        assertThat(persistentEntity.getUnderlyingAgreementContainer()).isEqualTo(newContainer);
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().stream()
                .allMatch(f -> f.getStatus().equals(FacilityStatus.LIVE))).isTrue();
        assertThat(persistentEntity.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().stream()
                .allMatch(f -> f.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption().getVariableEnergyConsumptionDataByProduct()
                        .stream().allMatch(p -> p.getProductStatus().equals(ProductStatus.LIVE)))).isTrue();
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_2).getConsolidationNumber()).isEqualTo(3);
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_2).getTerminatedDate()).isNull();
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_3).getConsolidationNumber()).isEqualTo(1);
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_3).getActivationDate()).isNotNull();
        assertThat(persistentEntity.getDocumentForSchemeVersion(SchemeVersion.CCA_3).getTerminatedDate()).isNull();

        verify(underlyingAgreementValidatorService, times(1)).validate(newContainer, underlyingAgreementValidationContext);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1))
				.calculateSchemeVersionsFromActiveFacilities(newContainer.getUnderlyingAgreement().getFacilities());
    }

    @Test
    void updateUnderlyingAgreement_not_found() {
        final long accountId = 1L;
        final UnderlyingAgreementContainer newContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .build();

        when(underlyingAgreementRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementService.updateUnderlyingAgreement(newContainer, accountId, underlyingAgreementValidationContext, true));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(underlyingAgreementValidatorService, times(1)).validate(newContainer, underlyingAgreementValidationContext);
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void setFileDocumentUuid() {
        final long underlyingAgreementId = 1L;
        final String fileDocumentUuid = UUID.randomUUID().toString();

        // Invoke
        underlyingAgreementService.saveFileDocumentUuid(underlyingAgreementId, fileDocumentUuid);

        // Verify
        verify(underlyingAgreementDocumentRepository, times(1))
                .updateFileDocumentUuid(underlyingAgreementId, fileDocumentUuid);
    }

    @Test
    void terminateActiveUnaDocuments() {
        final long underlyingAgreementId = 1L;
        final long accountId = 2L;
        final LocalDateTime terminatedDate = LocalDateTime.now();

        UnderlyingAgreementDocument unaDocument = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_2);
        UnderlyingAgreementEntity persistentEntity = UnderlyingAgreementEntity.builder()
                .id(underlyingAgreementId)
                .accountId(accountId)
                .build();
        persistentEntity.addUnderlyingAgreementDocument(unaDocument);

        when(underlyingAgreementRepository.findByAccountId(accountId)).thenReturn(Optional.of(persistentEntity));

        // Invoke
        underlyingAgreementService.terminateActiveUnaDocuments(accountId, terminatedDate);

        // Verify
        verify(underlyingAgreementRepository, times(1)).findByAccountId(accountId);
    }

    private void activateFacilities(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> f.setStatus(FacilityStatus.LIVE));
    }

    private void activateVariableEnergyProducts(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> f.getFacilityItem().getCca3BaselineAndTargets()
                .getFacilityBaselineEnergyConsumption().getVariableEnergyConsumptionDataByProduct()
                .forEach(p -> p.setProductStatus(ProductStatus.LIVE)));
    }
}
