package uk.gov.cca.api.verificationbody.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.cca.api.verificationbody.domain.Address;
import uk.gov.cca.api.verificationbody.domain.dto.AddressDTO;
import uk.gov.cca.api.common.EmissionTradingScheme;
import uk.gov.cca.api.verificationbody.service.AccreditationRefNumValidationService;
import uk.gov.cca.api.verificationbody.service.VerificationBodyUpdateService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.verificationbody.domain.VerificationBody;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyUpdateDTO;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyUpdateStatusDTO;
import uk.gov.cca.api.verificationbody.domain.event.VerificationBodyStatusDisabledEvent;
import uk.gov.cca.api.verificationbody.domain.event.VerificationBodyStatusEnabledEvent;
import uk.gov.cca.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.cca.api.verificationbody.event.AccreditationEmissionTradingSchemeNotAvailableEvent;
import uk.gov.cca.api.verificationbody.repository.VerificationBodyRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyUpdateServiceTest {
    
    @InjectMocks
    private VerificationBodyUpdateService service;
    
    @Mock
    private VerificationBodyRepository verificationBodyRepository;
    
    @Mock
    private AccreditationRefNumValidationService accreditationRefNumValidationService;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void updateVerificationBody() {
        Long verificationBodyId = 1L;
        
        VerificationBody verificationBody =
                VerificationBody.builder()
                    .id(verificationBodyId)
                    .name("nameOld")
                    .accreditationReferenceNumber("accreditationRefNumOld")
                    .address(Address.builder().city("cityOld").country("countryOld").line1("lineOld").build())
                    .build();

        EmissionTradingScheme emissionTradingScheme1 = mock(EmissionTradingScheme.class);
        EmissionTradingScheme emissionTradingScheme2 = mock(EmissionTradingScheme.class);

        Set<EmissionTradingScheme> emissionTradingSchemes = new HashSet<>();
        emissionTradingSchemes.add(emissionTradingScheme1);
        verificationBody.setEmissionTradingSchemes(emissionTradingSchemes);

        Set<EmissionTradingScheme> schemes = new HashSet<>();
        schemes.add(emissionTradingScheme2);

        VerificationBodyUpdateDTO verificationBodyUpdateDTO =
                VerificationBodyUpdateDTO.builder()
                    .id(verificationBodyId)
                    .verificationBody(
                            VerificationBodyEditDTO.builder()
                                .name("nameNew")
                                .accreditationReferenceNumber("accreditationRefNumNew")
                                .address(AddressDTO.builder().city("cityNew").country("countryNew").line1("lineNew").build())
                                .emissionTradingSchemes(schemes)
                                .build()
                            )
                    .build();
        
        
        when(verificationBodyRepository.findByIdEagerEmissionTradingSchemes(verificationBodyId))
            .thenReturn(Optional.of(verificationBody));
        
        //invoke
        service.updateVerificationBody(verificationBodyUpdateDTO);
        
        assertThat(verificationBody.getName()).isEqualTo(verificationBodyUpdateDTO.getVerificationBody().getName());
        assertThat(verificationBody.getAddress().getCity()).isEqualTo(verificationBodyUpdateDTO.getVerificationBody().getAddress().getCity());
        assertThat(verificationBody.getAddress().getCountry()).isEqualTo(verificationBodyUpdateDTO.getVerificationBody().getAddress().getCountry());
        assertThat(verificationBody.getAddress().getLine1()).isEqualTo(verificationBodyUpdateDTO.getVerificationBody().getAddress().getLine1());
        assertThat(verificationBody.getEmissionTradingSchemes()).containsExactlyInAnyOrder(emissionTradingScheme2);

        verify(verificationBodyRepository, times(1)).findByIdEagerEmissionTradingSchemes(verificationBodyId);
        verify(accreditationRefNumValidationService, times(1))
            .validate(verificationBodyUpdateDTO.getVerificationBody().getAccreditationReferenceNumber(), verificationBodyId);
        verify(eventPublisher, times(1))
            .publishEvent(new AccreditationEmissionTradingSchemeNotAvailableEvent(verificationBodyId, Set.of(emissionTradingScheme1)));
        
    }

    @Test
    void updateVerificationBodiesStatus() {
        VerificationBodyUpdateStatusDTO updateStatus1 = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();
        VerificationBodyUpdateStatusDTO updateStatus2 = VerificationBodyUpdateStatusDTO.builder()
                .id(2L).status(VerificationBodyStatus.DISABLED).build();
        VerificationBody vb1 = VerificationBody.builder()
                .id(1L).status(VerificationBodyStatus.DISABLED).build();
        VerificationBody vb2 = VerificationBody.builder()
                .id(2L).status(VerificationBodyStatus.ACTIVE).build();

        // Mock
        when(verificationBodyRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(vb1));
        when(verificationBodyRepository.findAllByIdIn(Set.of(2L))).thenReturn(List.of(vb2));

        // Invoke
        service.updateVerificationBodiesStatus(List.of(updateStatus1, updateStatus2));

        // Assert
        assertEquals(VerificationBodyStatus.ACTIVE, vb1.getStatus());
        assertEquals(VerificationBodyStatus.DISABLED, vb2.getStatus());
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(1L));
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(2L));
        verify(eventPublisher, times(1)).publishEvent(new VerificationBodyStatusEnabledEvent(Set.of(1L)));
        verify(eventPublisher, times(1)).publishEvent(new VerificationBodyStatusDisabledEvent(Set.of(2L)));

    }

    @Test
    void updateVerificationBodiesStatus_only_active() {
        VerificationBodyUpdateStatusDTO updateStatus1 = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();
        VerificationBody vb1 = VerificationBody.builder()
                .id(1L).status(VerificationBodyStatus.DISABLED).build();

        // Mock
        when(verificationBodyRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(vb1));

        // Invoke
        service.updateVerificationBodiesStatus(List.of(updateStatus1));

        // Assert
        assertEquals(VerificationBodyStatus.ACTIVE, vb1.getStatus());
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(1L));
        verify(eventPublisher, times(1)).publishEvent(new VerificationBodyStatusEnabledEvent(Set.of(1L)));
        verify(eventPublisher, never()).publishEvent(new VerificationBodyStatusDisabledEvent(anySet()));
    }

    @Test
    void updateVerificationBodiesStatus_only_disabled() {
        VerificationBodyUpdateStatusDTO updateStatus2 = VerificationBodyUpdateStatusDTO.builder()
                .id(2L).status(VerificationBodyStatus.DISABLED).build();
        VerificationBody vb2 = VerificationBody.builder()
                .id(2L).status(VerificationBodyStatus.ACTIVE).build();

        // Mock
        when(verificationBodyRepository.findAllByIdIn(Set.of(2L))).thenReturn(List.of(vb2));

        // Invoke
        service.updateVerificationBodiesStatus(List.of(updateStatus2));

        // Assert
        assertEquals(VerificationBodyStatus.DISABLED, vb2.getStatus());
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(2L));
        verify(eventPublisher, never()).publishEvent(new VerificationBodyStatusEnabledEvent(anySet()));
        verify(eventPublisher, times(1)).publishEvent(new VerificationBodyStatusDisabledEvent(Set.of(2L)));
    }

    @Test
    void updateVerificationBodiesStatus_same_status_disabled() {
        VerificationBodyUpdateStatusDTO updateStatus1 = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();
        VerificationBodyUpdateStatusDTO updateStatus2 = VerificationBodyUpdateStatusDTO.builder()
                .id(2L).status(VerificationBodyStatus.DISABLED).build();

        VerificationBody vb1 = VerificationBody.builder()
                .id(1L).status(VerificationBodyStatus.DISABLED).build();
        VerificationBody vb2 = VerificationBody.builder()
                .id(1L).status(VerificationBodyStatus.DISABLED).build();


        // Mock
        when(verificationBodyRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(vb1));
        when(verificationBodyRepository.findAllByIdIn(Set.of(2L))).thenReturn(List.of(vb2));

        // Invoke
        service.updateVerificationBodiesStatus(List.of(updateStatus1, updateStatus2));

        // Assert
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(1L));
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(2L));
        verifyNoMoreInteractions(verificationBodyRepository);
        verify(eventPublisher, times(1)).publishEvent(new VerificationBodyStatusEnabledEvent(Set.of(1L)));
        verify(eventPublisher, never()).publishEvent(new VerificationBodyStatusDisabledEvent(anySet()));
    }

    @Test
    void updateVerificationBodiesStatus_same_status_active() {
        VerificationBodyUpdateStatusDTO updateStatus1 = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();
        VerificationBodyUpdateStatusDTO updateStatus2 = VerificationBodyUpdateStatusDTO.builder()
                .id(2L).status(VerificationBodyStatus.DISABLED).build();

        VerificationBody vb1 = VerificationBody.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();

        VerificationBody vb2 = VerificationBody.builder()
                .id(2L).status(VerificationBodyStatus.ACTIVE).build();

        // Mock
        when(verificationBodyRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of(vb1));
        when(verificationBodyRepository.findAllByIdIn(Set.of(2L))).thenReturn(List.of(vb2));

        // Invoke
        service.updateVerificationBodiesStatus(List.of(updateStatus1, updateStatus2));

        // Assert
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(1L));
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(2L));
        verifyNoMoreInteractions(verificationBodyRepository);
        verify(eventPublisher, never()).publishEvent(new VerificationBodyStatusEnabledEvent(anySet()));
        verify(eventPublisher, times(1)).publishEvent(new VerificationBodyStatusDisabledEvent(Set.of(2L)));
    }

    @Test
    void updateVerificationBodiesStatus_active_vb_deleted() {
        VerificationBodyUpdateStatusDTO updateStatus1 = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();

        // Mock
        when(verificationBodyRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.updateVerificationBodiesStatus(Arrays.asList(updateStatus1)));

        // Assert
        assertEquals(ErrorCode.VERIFICATION_BODY_DOES_NOT_EXIST, businessException.getErrorCode());

        // Assert
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(1L));
        verify(verificationBodyRepository, never()).saveAll(anyList());
        verifyNoMoreInteractions(verificationBodyRepository);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateVerificationBodiesStatus_disabled_vb_deleted() {
        VerificationBodyUpdateStatusDTO updateStatus1 = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.DISABLED).build();

        // Mock
        when(verificationBodyRepository.findAllByIdIn(Set.of(1L))).thenReturn(List.of());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.updateVerificationBodiesStatus(Arrays.asList(updateStatus1)));

        // Assert
        assertEquals(ErrorCode.VERIFICATION_BODY_DOES_NOT_EXIST, businessException.getErrorCode());

        // Assert
        verify(verificationBodyRepository, times(1)).findAllByIdIn(Set.of(1L));
        verify(verificationBodyRepository, never()).saveAll(anyList());
        verifyNoMoreInteractions(verificationBodyRepository);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
