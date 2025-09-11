import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AuthorisationAndAdditionalEvidence, UnderlyingAgreementReviewDecision } from 'cca-api';

import { AuthorisationAdditionalEvidenceWizardStep } from '../types';
import { addDecisionSummaryData } from './decision-summary-data';

function toSummaryData(
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence,
  underlyingAgreementAttachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  prefix = '../',
): SummaryFactory {
  return new SummaryFactory()
    .addSection('', prefix + AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE)
    .addFileListRow(
      'Authorisation',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(
          authorisationAndAdditionalEvidence.authorisationAttachmentIds,
          underlyingAgreementAttachments,
        ),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Additional evidence',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(
          authorisationAndAdditionalEvidence.additionalEvidenceAttachmentIds,
          underlyingAgreementAttachments,
        ),
        downloadUrl,
      ),
      { change: isEditable },
    );
}

export function toAuthorisationAdditionalEvidenceSummaryData(
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence,
  underlyingAgreementAttachments: Record<string, string>,
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
  underlyingAgreementAttachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
  decision: UnderlyingAgreementReviewDecision,
  reviewAttachments: Record<string, string>,
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
