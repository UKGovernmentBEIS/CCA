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
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    RouterLink,
    SummaryQueryParamsPipe,
    SummaryDownloadFilesComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  protected readonly data = input.required<SummaryData>();
  protected readonly displayData = computed(() => filterEmptySections(this.data()));
}

const filterEmptySections = (sectionList: SummaryData): SummaryData =>
  sectionList.filter((s) => s.data.filter(hasValues).length || s.header);

const hasValues = (v: SummarySection) => {
  // password values are always shown
  if (v.key === 'Password') return true;
  // file list rows are always shown to display 'No files provided'
  if (v.isFileList) return true;
  if (Array.isArray(v.value)) {
    return v.value.length && v.value.some(Boolean);
  }
  return Boolean(v.value);
};
