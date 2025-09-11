import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  TaskItemStatus,
  TasksApiService,
  toVariationDetailsSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps, setVariationDetailsReviewSection } from '../../../utils';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Variation details">Check your answers</netz-page-heading>
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
export default class VariationDetailsCheckYourAnswersComponent {
  protected readonly store = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);

  protected readonly summaryData = toVariationDetailsSummaryData(
    this.store.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[VARIATION_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const currReviewProps = extractReviewProps(this.store);

    // as per [spec](https://trasys.atlassian.net/wiki/spaces/CCA/pages/11895061/Underlying+Agreement+Variation+Determination+Step+Regulator#Elements-and-Labels)
    // the variation details section is always accepted, so we need to set the review section completed and the decision accordingly

    const reviewProps = setVariationDetailsReviewSection(currReviewProps);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted, {
      ...currReviewProps,
      ...reviewProps,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
    });
  }
}
