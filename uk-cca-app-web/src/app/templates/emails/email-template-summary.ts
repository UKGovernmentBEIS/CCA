import { DatePipe } from '@angular/common';

import { LinkList, SummaryData, SummaryFactory } from '@shared/components';

import { NotificationTemplateViewDTO } from 'cca-api';

export function toEmailTemplateSummary(dto: NotificationTemplateViewDTO, isEditable: boolean): SummaryData {
  const factory = new SummaryFactory();

  if (!isEditable) {
    factory
      .addSection('Template Content', 'edit')
      .addChangeRow('Email subject', dto?.subject, { appendChangeParam: false })
      .addTextAreaRow('Email message', dto?.text, { change: true, appendChangeParam: false });
  }

  const documentsList: LinkList = dto?.documentTemplates.map((dt) => ({
    link: `/templates/document/${dt.id}`,
    text: dt.name,
  }));

  factory
    .addSection('Details')
    .addRow('Event trigger', dto?.eventTrigger)
    .addRow('Workflow', dto?.workflow)
    .addLinkListRow('Attached document', documentsList)
    .addRow('Last changed', new DatePipe('en-GB').transform(dto?.lastUpdatedDate, 'd MMM yyyy'));

  return factory.create();
}
