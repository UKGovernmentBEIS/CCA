import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { AccountSearchResultInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-target-unit-accounts-list',
  template: `
    <div class="govuk-grid-row">
      <govuk-table [columns]="tableColumns" [data]="accounts()" data-testid="accounts-list-table">
        <ng-template let-column="column" let-index="index" let-row="row">
          @if (column.field === 'name') {
            <a [routerLink]="[row.id]" class="govuk-link">{{ row[column.field] }}</a>
          } @else if (column.field === 'status') {
            <govuk-tag [color]="row[column.field] | statusColor">
              {{ row[column.field] | status }}
            </govuk-tag>
          } @else {
            {{ row[column.field] }}
          }
        </ng-template>
      </govuk-table>
    </div>
  `,
  imports: [TagComponent, TableComponent, StatusPipe, RouterLink, StatusColorPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitAccountsListComponent {
  protected readonly accounts = input<AccountSearchResultInfoDTO[]>();

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'name', header: 'Target unit name', widthClass: 'govuk-!-width-one-third' },
    { field: 'businessId', header: 'ID', widthClass: 'govuk-!-width-one-third' },
    { field: 'status', header: 'Status', widthClass: 'govuk-!-width-one-third' },
  ];
}
