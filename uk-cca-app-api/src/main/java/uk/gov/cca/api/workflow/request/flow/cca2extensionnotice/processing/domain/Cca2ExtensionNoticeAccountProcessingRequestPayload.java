package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingRequestPayload extends CcaRequestPayload {

    private String defaultSignatory;

    private AccountReferenceData accountReferenceData;

    private UnderlyingAgreement underlyingAgreement;

    private FileInfoDTO officialNotice;

    private FileInfoDTO underlyingAgreementDocument;

    private List<DefaultNoticeRecipient> defaultContacts;
}
