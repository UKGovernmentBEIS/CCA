import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { CorrectiveActionFollowUpResponse } from 'cca-api';

import { trackCorrectiveActionsQuery } from '../../../track-corrective-actions.selectors';

export type TrackCorrectiveActionIsCarriedOutFormModel = FormGroup<{
  isActionCarriedOut: FormControl<CorrectiveActionFollowUpResponse['isActionCarriedOut']>;
  comments: FormControl<CorrectiveActionFollowUpResponse['comments']>;
}>;

export const TRACK_CORRECTIVE_ACTION_IS_CARRIED_OUT_FORM =
  new InjectionToken<TrackCorrectiveActionIsCarriedOutFormModel>('Track corrective action is carried out form');

export const TrackCorrectiveActionIsCarriedOutFormProvider: Provider = {
  provide: TRACK_CORRECTIVE_ACTION_IS_CARRIED_OUT_FORM,
  deps: [FormBuilder, RequestTaskStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, activatedRoute: ActivatedRoute) => {
    const actionId = activatedRoute.snapshot.params.actionId;

    const action = store.select(trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions)()
      ?.correctiveActionResponses[actionId];

    const group = fb.group({
      isActionCarriedOut: new FormControl(action?.isActionCarriedOut ?? null, [
        GovukValidators.required('Make a selection'),
      ]),
      comments: new FormControl(action?.comments ?? null),
    });

    group.controls.isActionCarriedOut.valueChanges.pipe(takeUntilDestroyed()).subscribe((isCarriedOut) => {
      if (isCarriedOut) {
        group.controls.comments.disable();
      } else {
        group.controls.comments.reset();
        group.controls.comments.enable();
      }
    });

    return group;
  },
};
