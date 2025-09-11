import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toProvideEvidenceSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { underlyingAgreementActivationQuery } from '../../../+state/una-activation.selectors';

@Component({
  selector: 'cca-provide-evidence-summary',
  template: `
    <div>
      <netz-page-heading>Provide evidence</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProvideEvidenceSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = computed(() =>
    toProvideEvidenceSummaryData(
      this.requestTaskStore.select(underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationDetails)(),
      this.requestTaskStore.select(underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );
}
