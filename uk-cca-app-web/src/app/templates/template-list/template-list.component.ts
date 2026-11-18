import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { GovukTableColumn, TableComponent } from '@netz/govuk-components';

import { DocumentTemplateInfoDTO, NotificationTemplateInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-template-list',
  template: `
    <govuk-table [columns]="tableColumns" [data]="templates()" data-testid="template-list-table">
      <ng-template let-column="column" let-index="index" let-row="row">
        @if (column.field === 'name') {
          <a [routerLink]="[templateType(), row.id]" class="govuk-link">{{ row[column.field] }}</a>
        } @else if (column.field === 'lastUpdatedDate') {
          {{ row[column.field] | date: 'd MMM yyyy' }}
        } @else {
          {{ row[column.field] }}
        }
      </ng-template>
    </govuk-table>
  `,
  imports: [TableComponent, RouterLink, DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplateListComponent {
  protected readonly templates = input.required<NotificationTemplateInfoDTO[] | DocumentTemplateInfoDTO[]>();
  protected readonly templateType = input.required<'email' | 'document'>();

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'name', header: 'Template name' },
    { field: 'workflow', header: 'Workflow' },
    { field: 'lastUpdatedDate', header: 'Last changed' },
  ];
}
