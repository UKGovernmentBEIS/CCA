import { TitleCasePipe } from '@angular/common';

import { workflowType } from '@shared/pipes';

import { type RequestDetailsDTO } from 'cca-api';

const titleCasePipe = new TitleCasePipe();

export function transformWorkflowLabel(label: string): string {
  return (
    workflowType(label as RequestDetailsDTO['requestType']) ||
    titleCasePipe.transform(label.replaceAll('_', ' ')) ||
    label
  );
}
