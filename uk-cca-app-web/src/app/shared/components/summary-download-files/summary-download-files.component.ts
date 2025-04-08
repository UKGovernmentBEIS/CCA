import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { DownloadableFile } from '@shared/utils';

@Component({
  selector: 'cca-summary-download-files',
  template: `
    @for (file of files(); track file; let isLast = $last) {
      <a [routerLink]="file.downloadUrl" class="govuk-link" target="_blank">{{ file.fileName }}</a>

      @if (!isLast && files.length !== 1) {
        <br />
      }
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [RouterLink],
})
export class SummaryDownloadFilesComponent {
  files = input.required<DownloadableFile[]>();
}
