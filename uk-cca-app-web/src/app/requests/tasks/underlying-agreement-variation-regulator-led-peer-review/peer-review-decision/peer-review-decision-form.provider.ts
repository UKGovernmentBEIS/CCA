import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { FileUploadEvent } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { CcaPeerReviewDecision } from 'cca-api';

import { UnARegulatorLedVariationPeerReviewStore } from '../+state/underlying-agreement-variation-regulator-led-peer-review.store';

export type PeerReviewDecisionFormModel = FormGroup<{
  type: FormControl<CcaPeerReviewDecision['type'] | null>;
  notes: FormControl<string>;
  files: FormControl<FileUploadEvent | FileUploadEvent[]>;
}>;

export const PEER_REVIEW_DECISION_FORM = new InjectionToken<PeerReviewDecisionFormModel>('Peer review decision form');

export const PeerReviewDecisionFormProvider: Provider = {
  provide: PEER_REVIEW_DECISION_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService, UnARegulatorLedVariationPeerReviewStore],
  useFactory: createPeerReviewDecisionForm,
};

function createPeerReviewDecisionForm(
  fb: FormBuilder,
  requestTaskStore: RequestTaskStore,
  requestTaskFileService: RequestTaskFileService,
  peerReviewStore: UnARegulatorLedVariationPeerReviewStore,
): PeerReviewDecisionFormModel {
  const requestTaskId = requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

  const filesControl = requestTaskFileService.buildFormControl(
    requestTaskId,
    peerReviewStore.state.decision?.files || [],
    peerReviewStore.state.attachments,
    'PEER_REVIEW_UPLOAD_ATTACHMENT',
    false,
    !requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  return fb.group({
    type: fb.control(peerReviewStore.state.decision?.type ?? null, [
      GovukValidators.required('Select whether you agree or disagree with the determination'),
    ]),
    notes: fb.control(peerReviewStore.state.decision?.notes ?? null, [
      GovukValidators.required('Enter notes to support your decision'),
      GovukValidators.maxLength(10000, 'The notes should not be more than 10000 characters'),
    ]),
    files: filesControl,
  });
}
