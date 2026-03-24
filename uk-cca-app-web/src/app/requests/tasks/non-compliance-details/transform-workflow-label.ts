import { TitleCasePipe } from '@angular/common';

const titleCasePipe = new TitleCasePipe();

export function transformWorkflowLabel(label: string): string {
  return titleCasePipe.transform(label.replaceAll('_', ' ')) ?? label;
}
