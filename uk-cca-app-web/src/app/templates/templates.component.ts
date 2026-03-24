import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { TabLazyDirective, TabsComponent } from '@netz/govuk-components';

import { DocumentsComponent } from './documents/documents.component';
import { EmailsComponent } from './emails/emails.component';

@Component({
  selector: 'cca-templates',
  template: `
    <netz-page-heading size="xl">Templates</netz-page-heading>

    <govuk-tabs [queryParamsHandling]="'replace'">
      <ng-template govukTabLazy id="emails" label="Emails">
        <cca-emails />
      </ng-template>

      <ng-template govukTabLazy id="documents" label="Documents">
        <cca-documents />
      </ng-template>

      <!-- <ng-template govukTabLazy id="sector-templates" label="Sector templates">
        <cca-sector-templates />
      </ng-template> -->
    </govuk-tabs>
  `,
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    EmailsComponent,
    DocumentsComponent,
    // SectorTemplatesComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplatesComponent {}
