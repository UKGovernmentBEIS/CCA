import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { DocumentTemplatesService } from 'cca-api';

import { TemplateSearchComponent, TemplateSearchFetchFn } from '../template-search';

@Component({
  selector: 'cca-documents',
  template: `<cca-template-search [fetchFn]="fetchFn" templateType="document" fragment="documents" />`,
  imports: [TemplateSearchComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentsComponent {
  private readonly documentTemplatesService = inject(DocumentTemplatesService);

  protected readonly fetchFn: TemplateSearchFetchFn = (page, pageSize, term) =>
    this.documentTemplatesService.getCurrentUserDocumentTemplates(page, pageSize, [], term);
}
