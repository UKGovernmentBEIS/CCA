import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent } from '@netz/govuk-components';

import { RequestsService } from 'cca-api';

import { DocumentsComponent } from './documents/documents.component';
import { EmailsComponent } from './emails/emails.component';

@Component({
  selector: 'cca-templates',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-three-quarters">
        <netz-page-heading size="xl">Templates</netz-page-heading>
      </div>
      <div class="govuk-grid-column-one-quarter" style="text-align: end">
        <button class="govuk-!-margin-top-1" govukButton type="button" (click)="onNewCca2TerminationRun()">
          Start CCA 2 termination run
        </button>
      </div>
    </div>
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
    ButtonDirective,
    // SectorTemplatesComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplatesComponent {
  private readonly requestsService = inject(RequestsService);

  onNewCca2TerminationRun() {
    this.requestsService
      .processRequestCreateAction(
        {
          requestCreateActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
          requestType: 'CCA2_TERMINATION_RUN',
        },
        'ENGLAND',
      )
      .subscribe();
  }
}
