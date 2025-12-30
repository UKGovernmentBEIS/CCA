import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  PROVIDE_EVIDENCE_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toProvideEvidenceSummaryData,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementActivationRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO } from '../../../transform';
import { underlyingAgreementActivationQuery } from '../../../una-activation.selectors';

@Component({
  selector: 'cca-provide-evidence-check-answers',
  template: `
    <div>
      <netz-page-heading caption="Evidence">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData()" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
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
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = computed(() =>
    toProvideEvidenceSummaryData(
      this.requestTaskStore.select(underlyingAgreementActivationQuery.selectDetails)(),
      this.requestTaskStore.select(underlyingAgreementActivationQuery.selectAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementActivationRequestTaskPayload;

    const currentSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementActivationQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[PROVIDE_EVIDENCE_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
