import { SummaryData, SummaryFactory } from '@shared/components/summary';
import { transformAttachmentsToDownloadableFiles } from '@shared/utils';

import { AuthorisationAndAdditionalEvidence, UnderlyingAgreementReviewDecision } from 'cca-api';

import { AuthorisationAdditionalEvidenceWizardStep } from '../underlying-agreement.types';
import { addDecisionSummaryData } from './decision-summary-data';

function toSummaryData(
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence,
  underlyingAgreementAttachments: { [key: string]: string },
  isEditable: boolean,
  downloadUrl: string,
  prefix: string = '../',
): SummaryFactory {
  return new SummaryFactory()
    .addSection('', prefix + AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE)
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsToDownloadableFiles(
        authorisationAndAdditionalEvidence.authorisationAttachmentIds,
        underlyingAgreementAttachments,
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Uploaded files',
      transformAttachmentsToDownloadableFiles(
        authorisationAndAdditionalEvidence.additionalEvidenceAttachmentIds,
        underlyingAgreementAttachments,
        downloadUrl,
      ),
      { change: isEditable },
    );
}

export function toAuthorisationAdditionalEvidenceSummaryData(
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence,
  underlyingAgreementAttachments: { [key: string]: string },
  isEditable: boolean,
  downloadUrl: string,
  prefix = '../',
): SummaryData {
  return toSummaryData(
    authorisationAndAdditionalEvidence,
    underlyingAgreementAttachments,
    isEditable,
    downloadUrl,
    prefix,
  ).create();
}
export function toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence,
  underlyingAgreementAttachments: { [key: string]: string },
  isEditable: boolean,
  downloadUrl: string,
  decision: UnderlyingAgreementReviewDecision,
  reviewAttachments: { [key: string]: string },
  prefix = '../',
): SummaryData {
  const factory = toSummaryData(
    authorisationAndAdditionalEvidence,
    underlyingAgreementAttachments,
    isEditable,
    downloadUrl,
    prefix,
  );

  return addDecisionSummaryData(factory, decision, reviewAttachments, isEditable, downloadUrl).create();
}
