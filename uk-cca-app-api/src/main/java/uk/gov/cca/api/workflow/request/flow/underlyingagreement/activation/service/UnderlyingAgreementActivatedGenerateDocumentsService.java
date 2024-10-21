package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.UnderlyingAgreementCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.UnderlyingAgreementOfficialNoticeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;


@Log4j2
@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedGenerateDocumentsService {
	
	private final RequestService requestService;
	private final UnderlyingAgreementService underlyingAgreementService;
	private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
	private final UnderlyingAgreementCreateDocumentService underlyingAgreementCreateDocumentService;
	private final UnderlyingAgreementOfficialNoticeService underlyingAgreementOfficialNoticeService;
	
	@Transactional
	public void generateDocuments(String requestId) {
		CompletableFuture<FileInfoDTO> underlyingAgreementDocumentFuture = null;
		CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
		CompletableFuture<Void> allFutures = null;
		
		try {
			underlyingAgreementDocumentFuture = underlyingAgreementCreateDocumentService.create(requestId);
			officialNoticeFuture = underlyingAgreementOfficialNoticeService.generateActivatedOfficialNotice(requestId);
			
			allFutures = CompletableFuture.allOf(underlyingAgreementDocumentFuture, officialNoticeFuture);
        	allFutures.get();
			
			final FileInfoDTO underlyingAgreementDocument = underlyingAgreementDocumentFuture.get();
			final FileInfoDTO officialNotice = officialNoticeFuture.get();
			
			final Request request = requestService.findRequestById(requestId);
	        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
	        final UnderlyingAgreementDTO underlyingAgreementDTO = 
	        		underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(request.getAccountId());

			requestPayload.setUnderlyingAgreementDocument(underlyingAgreementDocument);
			requestPayload.setOfficialNotice(officialNotice);
			underlyingAgreementService.saveFileDocumentUuid(underlyingAgreementDTO.getId(), underlyingAgreementDocument.getUuid());
		} catch (ExecutionException e) {
			Throwable caused = e.getCause();
			if(caused.getClass() == BusinessException.class) {
				throw (BusinessException)caused;
			} else {
				log.error(caused.getMessage());
				throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
			}
		} catch (InterruptedException e) {
			Throwable caused = e.getCause();
			log.error(e.getMessage());
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
		} catch (Exception e) {
			Throwable caused = e.getCause();
			log.error(e.getMessage());
			throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
		} finally {
			ConcurrencyUtils.completeCompletableFutures(underlyingAgreementDocumentFuture, officialNoticeFuture, allFutures);
		}
	}
}
