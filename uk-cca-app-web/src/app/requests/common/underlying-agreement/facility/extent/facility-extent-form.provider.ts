import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { RequestTaskFileService } from '@shared/services';

import { FacilityExtent } from 'cca-api';

import { underlyingAgreementQuery } from '../../+state/underlying-agreement.selectors';
import { getAttachmentType, UPLOAD_SECTION_ATTACHMENT_TYPE } from '../../types';

export type FacilityExtentFormModel = {
  manufacturingProcessFile: FormControl<UuidFilePair>;
  processFlowFile: FormControl<UuidFilePair>;
  annotatedSitePlansFile: FormControl<UuidFilePair>;
  eligibleProcessFile: FormControl<UuidFilePair>;
  areActivitiesClaimed: FormControl<FacilityExtent['areActivitiesClaimed']>;
  activitiesDescriptionFile: FormControl<UuidFilePair>;
};

export const FACILITY_EXTENT_FORM = new InjectionToken<FacilityExtentFormModel>('Facility Extent Form');

export const FacilityExtentFormProvider: Provider = {
  provide: FACILITY_EXTENT_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    requestTaskStore: RequestTaskStore,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();
    const facilityId = activatedRoute.snapshot.params.facilityId;

    const facilityExtent = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))()
      ?.facilityExtent;

    const attachments = requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

    return fb.group({
      manufacturingProcessFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        facilityExtent?.manufacturingProcessFile,
        attachments,
        getAttachmentType(UPLOAD_SECTION_ATTACHMENT_TYPE, requestTaskType),
        true,
        false,
      ),
      processFlowFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        facilityExtent?.processFlowFile,
        attachments,
        getAttachmentType(UPLOAD_SECTION_ATTACHMENT_TYPE, requestTaskType),
        true,
        false,
      ),
      annotatedSitePlansFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        facilityExtent?.annotatedSitePlansFile,
        attachments,
        getAttachmentType(UPLOAD_SECTION_ATTACHMENT_TYPE, requestTaskType),
        true,
        false,
      ),
      eligibleProcessFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        facilityExtent?.eligibleProcessFile,
        attachments,
        getAttachmentType(UPLOAD_SECTION_ATTACHMENT_TYPE, requestTaskType),
        true,
        false,
      ),
      areActivitiesClaimed: fb.control(facilityExtent?.areActivitiesClaimed ?? null, [
        GovukValidators.required('Select yes if you are claiming any directly associated activities'),
      ]),
      activitiesDescriptionFile: requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        facilityExtent?.activitiesDescriptionFile,
        attachments,
        getAttachmentType(UPLOAD_SECTION_ATTACHMENT_TYPE, requestTaskType),
        true,
        false,
      ),
    });
  },
};
