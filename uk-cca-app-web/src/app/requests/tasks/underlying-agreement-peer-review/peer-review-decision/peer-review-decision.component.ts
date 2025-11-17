import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';

import { UnderlyingAgreementPeerReviewState, UnderlyingAgreementPeerReviewStore } from '../+state';
import {
  PEER_REVIEW_DECISION_FORM,
  PeerReviewDecisionFormModel,
  PeerReviewDecisionFormProvider,
} from './peer-review-decision-form.provider';

@Component({
  selector: 'cca-peer-review-decision',
  templateUrl: './peer-review-decision.component.html',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    WizardStepComponent,
    RadioComponent,
    RadioOptionComponent,
    TextareaComponent,
    MultipleFileInputComponent,
    RouterLink,
  ],
  providers: [PeerReviewDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly peerReviewStore = inject(UnderlyingAgreementPeerReviewStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<PeerReviewDecisionFormModel>(PEER_REVIEW_DECISION_FORM);

  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
  protected readonly downloadUrl = generateDownloadUrl(this.taskId.toString());

  onSubmit() {
    const filesValue = this.form.value.files;
    const files = Array.isArray(filesValue) ? filesValue : filesValue ? [filesValue] : [];
    const uuidFilePairs = files.map((file) => ({
      uuid: file.uuid,
      file: file.file,
    }));

    const statePayload: UnderlyingAgreementPeerReviewState = {
      decision: {
        type: this.form.value.type,
        notes: this.form.value.notes,
        files: fileUtils.toUUIDs(uuidFilePairs),
      },
      attachments: fileUtils.toAttachments(uuidFilePairs),
    };

    this.peerReviewStore.setState(statePayload);
    this.router.navigate(['check-your-answers'], {
      relativeTo: this.activatedRoute,
    });
  }
}
