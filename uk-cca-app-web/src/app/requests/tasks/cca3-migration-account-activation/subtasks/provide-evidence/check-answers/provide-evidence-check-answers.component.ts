import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toProvideEvidenceSummaryData,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { cca3MigrationAccountActivationQuery } from '../../../+state/cca3-migration-account-activation.selectors';
import { createSaveActionDTO } from '../../../transform';

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
      this.requestTaskStore.select(cca3MigrationAccountActivationQuery.selectCca3MigrationAccountActivationDetails)(),
      this.requestTaskStore.select(
        cca3MigrationAccountActivationQuery.selectCca3MigrationAccountActivationAttachments,
      )(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(cca3MigrationAccountActivationQuery.selectPayload)();

    const sectionsCompleted = produce(
      this.requestTaskStore.select(cca3MigrationAccountActivationQuery.selectSectionsCompleted)(),
      (draft) => {
        draft[CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK] = TaskItemStatus.COMPLETED;
      },
    );

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSaveActionDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute });
    });
  }
}
