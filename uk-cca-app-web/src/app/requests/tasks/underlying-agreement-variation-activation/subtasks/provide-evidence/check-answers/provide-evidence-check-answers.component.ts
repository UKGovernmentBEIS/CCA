import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { PROVIDE_EVIDENCE_SUBTASK, toProvideEvidenceSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { underlyingAgreementVariationActivationQuery } from '../../../+state/una-variation-activation.selectors';

@Component({
  selector: 'cca-provide-evidence-check-answers',
  templateUrl: './provide-evidence-check-answers.component.html',
  standalone: true,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProvideEvidenceCheckAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = computed(() =>
    toProvideEvidenceSummaryData(
      this.requestTaskStore.select(
        underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationDetails,
      )(),
      this.requestTaskStore.select(
        underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationAttachments,
      )(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  onSubmit() {
    this.taskService
      .submitSubtask(PROVIDE_EVIDENCE_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute }));
  }
}
