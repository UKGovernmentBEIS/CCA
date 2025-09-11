import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  TasksApiService,
  toAuthorisationAdditionalEvidenceSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Authorisation and additional evidence">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceCheckYourAnswersComponent {
  protected readonly store = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);

  private readonly taskId = this.route.snapshot.paramMap.get('taskId');
  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAuthorisationAdditionalEvidenceSummaryData(
    this.store.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);

    // We don't need to update the payload here because we're just marking the section as completed
    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    // Mark the section as completed
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = 'COMPLETED';
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.route });
    });
  }
}
