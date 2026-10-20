import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

import { GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { DownloadableFile } from '@shared/utils';

type SectorTemplate = {
  id?: number;
  process?: string;
  templateFile?: DownloadableFile;
  guidanceFile?: DownloadableFile;
};

const SECTOR_TEMPLATES: SectorTemplate[] = [
  {
    id: 1,
    process: 'Target period reporting bulk upload',
    templateFile: {
      fileName: 'TPR file template.csv',
      downloadUrl: 'assets/files/sector-templates/tpr-template.csv',
    },
    guidanceFile: {
      fileName: 'Guidance on completing the TPR file.pdf',
      downloadUrl: 'assets/files/sector-templates/tpr-guidance.pdf',
    },
  },
  {
    id: 2,
    process: 'PAT reports',
    templateFile: {
      fileName: 'PAT file template.csv',
      downloadUrl: 'assets/files/sector-templates/pat-template.csv',
    },
    guidanceFile: {
      fileName: 'Guidance on completing PAT file.pdf',
      downloadUrl: 'assets/files/sector-templates/pat-guidance.pdf',
    },
  },
];

@Component({
  selector: 'cca-sector-templates',
  template: `
    <govuk-table [columns]="tableColumns" [data]="templates()">
      <ng-template let-column="column" let-index="index" let-row="row">
        @if (column.field === 'process') {
          {{ row[column.field] }}
        } @else {
          <a [href]="row[column.field]?.downloadUrl" class="govuk-link" target="_blank" download>
            {{ row[column.field]?.fileName }}
          </a>
        }
      </ng-template>
    </govuk-table>
  `,
  imports: [TableComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorTemplatesComponent {
  protected readonly templates = signal<SectorTemplate[]>(SECTOR_TEMPLATES);

  protected readonly tableColumns: GovukTableColumn[] = [
    { field: 'process', header: 'Process' },
    { field: 'templateFile', header: 'Template file' },
    { field: 'guidanceFile', header: 'Guidance file' },
  ];
}
