import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { NonComplianceConclusionSubmitRequestTaskPayload, TasksService } from 'cca-api';

import { ProvideAppealDetailsStore } from '../+state';
import { toAppealDetailsSummaryData } from '../to-appeal-details-summary-data';

@Component({
  selector: 'cca-provide-appeal-details-check-your-answers',
  template: `
    <netz-page-heading>Check your answers</netz-page-heading>
    <cca-summary [data]="summaryData()" />
    @if (isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    }
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
export class CheckYourAnswersComponent {
  private readonly provideAppealDetailsStore = inject(ProvideAppealDetailsStore);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly state = this.provideAppealDetailsStore.stateAsSignal;
  private readonly requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly summaryData = computed(() => {
    const state = this.state();

    if (!state.appealDetails) {
      return [];
    }

    return toAppealDetailsSummaryData(
      state.appealDetails,
      state.attachments,
      this.isEditable(),
      generateDownloadUrl(this.requestTaskId()?.toString()),
    );
  });

  onSubmit() {
    if (!this.isEditable()) return;

    const { appealDetails } = this.state();
    if (!appealDetails) {
      this.router.navigate(['../provide-details'], { relativeTo: this.route });
      return;
    }

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS',
        requestTaskId: +this.requestTaskId(),
        requestTaskActionPayload: {
          payloadType: 'NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS_PAYLOAD',
          appealDetails,
        } as NonComplianceConclusionSubmitRequestTaskPayload,
      })
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route, replaceUrl: true }));
  }
}
