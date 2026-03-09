import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { RequestedDocuments } from 'cca-api';

export function toPreAuditReviewRequestedDocumentsSummaryData(
  requestedDocuments: RequestedDocuments,
  attachments: Record<string, string>,
  isEditable: boolean,
  downloadUrl: string,
): SummaryData {
  const datePipe = new DatePipe('en-GB');

  return new SummaryFactory()
    .addSection('', '../upload-documents')
    .addRow(
      'Date pre-audit reviewmaterial received',
      datePipe.transform(requestedDocuments?.auditMaterialReceivedDate, 'dd MMM yyyy'),
      { change: isEditable },
    )
    .addFileListRow(
      'Manufacturing process description document',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([requestedDocuments?.manufacturingProcessFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Process flow maps document',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([requestedDocuments?.processFlowMapsFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Annotated site plan',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([requestedDocuments?.annotatedSitePlansFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Elligible process description document',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([requestedDocuments?.eligibleProcessFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Directly associated activities document',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([requestedDocuments?.directlyAssociatedActivitiesFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'The 70% rule evidence document',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments([requestedDocuments?.seventyPerCentRuleEvidenceFile], attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Base year/target period evidence document',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(requestedDocuments?.baseYearTargetPeriodEvidenceFiles, attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addFileListRow(
      'Additional documents',
      fileUtils.toDownloadableFiles(
        fileUtils.extractAttachments(requestedDocuments?.additionalDocuments, attachments),
        downloadUrl,
      ),
      { change: isEditable },
    )
    .addTextAreaRow('Additional information', requestedDocuments?.additionalInformation, {
      change: isEditable,
    })
    .create();
}
