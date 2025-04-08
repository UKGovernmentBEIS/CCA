import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  toVariationDetailsSummaryData,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-check-your-answers',
  standalone: true,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './variation-details-check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VariationDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);

  protected readonly summaryData = toVariationDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  onSubmit() {
    this.taskService
      .submitSubtask(VARIATION_DETAILS_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
