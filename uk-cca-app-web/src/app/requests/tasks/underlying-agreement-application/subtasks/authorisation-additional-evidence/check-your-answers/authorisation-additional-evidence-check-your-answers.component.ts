import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  toAuthorisationAdditionalEvidenceSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

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
  templateUrl: './authorisation-additional-evidence-check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAuthorisationAdditionalEvidenceSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSubmit() {
    this.taskService
      .submitSubtask(AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
