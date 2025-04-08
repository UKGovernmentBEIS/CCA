import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

import { SummaryDownloadFilesComponent } from '../summary-download-files/summary-download-files.component';
import { SummaryQueryParamsPipe } from './queryParams.pipe';
import { SummaryData, SummarySection } from './type';

@Component({
  selector: 'cca-summary',
  templateUrl: './summary.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    NgClass,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    RouterLink,
    SummaryQueryParamsPipe,
    SummaryDownloadFilesComponent,
  ],
})
export class SummaryComponent {
  data = input.required<SummaryData>();
  displayData = computed(() => filterEmptySections(this.data()));
}

const filterEmptySections = (sectionList: SummaryData): SummaryData =>
  sectionList.filter((s) => s.data.filter(hasValues).length);

const hasValues = (v: SummarySection) => {
  // password values are always shown
  if (v.key === 'Password') return true;
  return Array.isArray(v.value) && v.value.length && v.value.some(Boolean);
};
