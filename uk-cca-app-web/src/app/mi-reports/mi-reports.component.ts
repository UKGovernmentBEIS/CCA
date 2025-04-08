import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukTableColumn, TableComponent } from '@netz/govuk-components';

import { createTablePage, miReportTypeDescriptionMap, miReportTypeLinkMap } from './core/mi-report';
import { MiReportsStore } from './store/mi-reports.store';

@Component({
  selector: 'cca-mi-reports',
  standalone: true,
  templateUrl: './mi-reports.component.html',
  imports: [PageHeadingComponent, TableComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportsComponent {
  private readonly store = inject(MiReportsStore);
  readonly pageSize = 10;
  readonly miReportTypeLinkMap = miReportTypeLinkMap;

  data = this.store.state;
  tableColumns: GovukTableColumn[] = [{ field: 'description', header: 'MI Report Type' }];
  currentPage = signal(1);

  currentPageData = computed(() => {
    return createTablePage(this.currentPage(), this.pageSize, this.data)
      .map((p) => ({
        ...p,
        description: miReportTypeDescriptionMap[p.miReportType],
      }))
      .sort((a, b) => a.description?.localeCompare(b.description));
  });
}
