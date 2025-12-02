import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { TRACK_CORRECTIVE_ACTIONS_UPLOAD_SECTION_ATTACHMENT_TYPE } from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { CorrectiveActionFollowUpResponse } from 'cca-api';

import { trackCorrectiveActionsQuery } from '../../../track-corrective-actions.selectors';

export type TrackCorrectiveActionDetailsFormModel = FormGroup<{
  actionCarriedOutDate: FormControl<Date>;
  comments: FormControl<CorrectiveActionFollowUpResponse['comments']>;
  evidenceFiles: FormControl<UuidFilePair[]>;
}>;

export const TRACK_CORRECTIVE_ACTION_DETAILS_FORM = new InjectionToken<TrackCorrectiveActionDetailsFormModel>(
  'Track corrective action details form',
);

export const TrackCorrectiveActionDetailsFormProvider: Provider = {
  provide: TRACK_CORRECTIVE_ACTION_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    store: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
    activatedRoute: ActivatedRoute,
  ) => {
    const actionId = activatedRoute.snapshot.params.actionId;

    const correctiveActionResponse = store.select(trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions)()
      ?.correctiveActionResponses[actionId];

    const attachments = store.select(trackCorrectiveActionsQuery.selectFacilityAuditAttachments)();

    const evidenceFilesControl = requestTaskFileService.buildFormControl(
      store.select(requestTaskQuery.selectRequestTaskId)(),
      correctiveActionResponse?.evidenceFiles || [],
      attachments || {},
      TRACK_CORRECTIVE_ACTIONS_UPLOAD_SECTION_ATTACHMENT_TYPE,
      false,
      !store.select(requestTaskQuery.selectIsEditable)(),
    );

    return fb.group({
      actionCarriedOutDate: fb.control(
        correctiveActionResponse?.actionCarriedOutDate
          ? new Date(correctiveActionResponse?.actionCarriedOutDate)
          : null,
        {
          validators: [GovukValidators.required('The date must be today or in the past')],
        },
      ),
      comments: fb.control(correctiveActionResponse?.comments ?? null, {
        validators: [
          GovukValidators.required('Enter comments about how the operator carried out the corrective action'),
        ],
      }),
      evidenceFiles: evidenceFilesControl,
    });
  },
};
