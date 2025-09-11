import { DatePipe } from '@angular/common';

import { SummaryData, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { DocumentTemplateViewDTO } from 'cca-api';

export function toDocumentTemplateSummary(dto: DocumentTemplateViewDTO, isEditable: boolean): SummaryData {
  const factory = new SummaryFactory();

  if (!isEditable) {
    factory
      .addSection('Template content', 'edit')
      .addFileListRow(
        'Uploaded files',
        fileUtils.toDownloadableDocument([{ name: dto?.filename, uuid: dto?.fileUuid }], 'file-download'),
        { change: true, appendChangeParam: false },
      );
  }

  factory
    .addSection('Details')
    .addLinkListRow('Email sending this document', [
      { text: dto?.notificationTemplate?.name, link: `/templates/email/${dto?.notificationTemplateId}` },
    ])
    .addRow('Workflow', dto?.workflow)
    .addRow('Last changed', new DatePipe('en-GB').transform(dto?.lastUpdatedDate, 'd MMM yyyy'));

  return factory.create();
}
