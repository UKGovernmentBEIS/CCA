package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementCalculateSchemeVersionsUtil;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationAcceptedGenerateDocumentsService {

	private final RequestService requestService;
	private final UnderlyingAgreementVariationCreateDocumentService underlyingAgreementVariationCreateDocumentService;
	private final UnderlyingAgreementVariationOfficialNoticeService underlyingAgreementVariationOfficialNoticeService;
	
	@Transactional
	public void generateDocuments(String requestId) {
		Map<SchemeVersion, CompletableFuture<FileInfoDTO>> underlyingAgreementDocumentFutureMap = new EnumMap<>(SchemeVersion.class);
		CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
		CompletableFuture<Void> allFutures = null;
		
		try {
			final Request request = requestService.findRequestById(requestId);
	        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
	        
	        Set<SchemeVersion> schemeVersions = UnderlyingAgreementCalculateSchemeVersionsUtil
	        		.calculateSchemeVersionsFromActiveFacilities(requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities());
	        
	        schemeVersions.forEach(version -> underlyingAgreementDocumentFutureMap.put(version, 
	        		underlyingAgreementVariationCreateDocumentService.create(requestId, version)));
	        
			officialNoticeFuture = underlyingAgreementVariationOfficialNoticeService.generateAcceptedOfficialNotice(requestId);
			
			List<CompletableFuture<?>> allFuturesList = new ArrayList<>(underlyingAgreementDocumentFutureMap.values());
			allFuturesList.add(officialNoticeFuture);
			
			allFutures = CompletableFuture.allOf(allFuturesList.toArray(CompletableFuture[]::new));
        	allFutures.get();
			
        	final Map<SchemeVersion, FileInfoDTO> underlyingAgreementDocumentsMap = new EnumMap<>(SchemeVersion.class);
            for (Map.Entry<SchemeVersion, CompletableFuture<FileInfoDTO>> entry : underlyingAgreementDocumentFutureMap.entrySet()) {
            	underlyingAgreementDocumentsMap.put(entry.getKey(), entry.getValue().get()); 
            }
			final FileInfoDTO officialNotice = officialNoticeFuture.get();

			requestPayload.setUnderlyingAgreementDocuments(underlyingAgreementDocumentsMap);
			requestPayload.setOfficialNotices(List.of(officialNotice));
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
			List<CompletableFuture<?>> allFuturesList = new ArrayList<>(underlyingAgreementDocumentFutureMap.values());
			allFuturesList.add(officialNoticeFuture);
			allFuturesList.add(allFutures);
			ConcurrencyUtils.completeCompletableFutures(allFuturesList.toArray(CompletableFuture[]::new));
		}
	}
}
