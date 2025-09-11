import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukTableColumn, TableComponent } from '@netz/govuk-components';

import { createTablePage, miReportTypeDescriptionMap, miReportTypeLinkMap } from './core/mi-report';
import { MiReportsStore } from './mi-reports.store';

@Component({
  selector: 'cca-mi-reports',
  template: `
    <netz-page-heading size="xl">MI Reports</netz-page-heading>

    <div class="overflow-auto overflow-auto-table govuk-!-padding-1">
      <govuk-table [columns]="tableColumns" [data]="currentPageData()">
        <ng-template let-column="column" let-row="row">
          @switch (column.field) {
            @default {
              <a [routerLink]="miReportTypeLinkMap[row.miReportType]" class="govuk-link">{{ row[column.field] }}</a>
            }
          }
        </ng-template>
      </govuk-table>
    </div>
  `,
  standalone: true,
  imports: [PageHeadingComponent, TableComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportsComponent {
  private readonly store = inject(MiReportsStore);
  readonly pageSize = 10;
  readonly miReportTypeLinkMap = miReportTypeLinkMap;

  protected readonly data = this.store.state;
  protected readonly tableColumns: GovukTableColumn[] = [{ field: 'description', header: 'MI Report Type' }];
  protected readonly currentPage = signal(1);

  protected readonly currentPageData = computed(() => {
    return createTablePage(this.currentPage(), this.pageSize, this.data)
      .map((p) => ({
        ...p,
        description: miReportTypeDescriptionMap[p.miReportType],
      }))
      .sort((a, b) => a.description?.localeCompare(b.description));
  });
}
