package uk.gov.cca.api.web.controller.buyoutsurplus;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionsViewControllerTest {

    @InjectMocks
    private BuyOutSurplusTransactionsViewController controller;
    
    @Mock
    private BuyOutSurplusQueryService service;
    
    @Test
    void getBuyOutSurplusTransactions_returns200WithBody() {
        BuyOutSurplusTransactionsListSearchCriteria criteria = new BuyOutSurplusTransactionsListSearchCriteria();
        BuyOutSurplusTransactionsListDTO expectedDto =
                BuyOutSurplusTransactionsListDTO.builder()
                        .transactions(Collections.emptyList())
                        .total(0L)
                        .build();
        
        AppUser appUser = mock(AppUser.class);
        
        when(service.getBuyOutSurplusTransactionsList(appUser, criteria)).thenReturn(expectedDto);
        
        ResponseEntity<BuyOutSurplusTransactionsListDTO> response =
                controller.getBuyOutSurplusTransactions(appUser, criteria);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), expectedDto);
        verify(service, times(1)).getBuyOutSurplusTransactionsList(appUser, criteria);
        verifyNoMoreInteractions(service);
    }
}

