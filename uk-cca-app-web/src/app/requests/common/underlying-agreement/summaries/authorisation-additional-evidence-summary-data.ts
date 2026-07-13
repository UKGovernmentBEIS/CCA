import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AuthorisationAndAdditionalEvidence, UnderlyingAgreementReviewDecision } from 'cca-api';

import { AuthorisationAdditionalEvidenceWizardStep } from '../types';
import { addDecisionSummaryData } from './decision-summary-data';

type ToAuthorisationAdditionalEvidenceSummaryDataArgs = {
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence;
  underlyingAgreementAttachments: Record<string, string>;
  isEditable: boolean;
  downloadUrl: string;
  prefix?: string;
};

type ToAuthorisationAdditionalEvidenceSummaryDataWithDecisionArgs = {
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence;
  underlyingAgreementAttachments: Record<string, string>;
  isEditable: boolean;
  downloadUrl: string;
  decision: UnderlyingAgreementReviewDecision;
  reviewAttachments: Record<string, string>;
  prefix?: string;
};

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
  args: ToAuthorisationAdditionalEvidenceSummaryDataArgs,
): SummaryData {
  return toSummaryData(
    args.authorisationAndAdditionalEvidence,
    args.underlyingAgreementAttachments,
    args.isEditable,
    args.downloadUrl,
    args.prefix ?? '../',
  ).create();
}

export function toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
  args: ToAuthorisationAdditionalEvidenceSummaryDataWithDecisionArgs,
): SummaryData {
  const factory = toSummaryData(
    args.authorisationAndAdditionalEvidence,
    args.underlyingAgreementAttachments,
    args.isEditable,
    args.downloadUrl,
    args.prefix ?? '../',
  );

  if (!args.decision?.type) return factory.create();

  return addDecisionSummaryData({
    factory,
    decision: args.decision,
    attachments: args.reviewAttachments,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
  }).create();
}
