import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  DateInputComponent,
  DetailsComponent,
  RadioComponent,
  RadioOptionComponent,
} from '@netz/govuk-components';
import {
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationRegulatorLedSavePayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import { CHARGE_DATE_FORM, ChargeDateFormModel, ChargeDateFormProvider } from './charge-date-form.provider';

@Component({
  selector: 'cca-charge-date',
  templateUrl: './charge-date.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    RouterLink,
    RadioComponent,
    RadioOptionComponent,
    DateInputComponent,
    DetailsComponent,
    ConditionalContentDirective,
  ],
  providers: [ChargeDateFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChargeDateComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<ChargeDateFormModel>>(CHARGE_DATE_FORM);
  protected readonly facilityId = this.route.snapshot.params.facilityId;

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const savePayload = toUnAVariationRegulatorLedSavePayload(payload);
    const updatedPayload = updateChargeStartDate(savePayload, this.facilityId, this.form);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, currentSectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.route });
    });
  }
}

function updateChargeStartDate(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  facilityId: string,
  form: FormGroup<ChargeDateFormModel>,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  const dateValue = form.value.hasChargeStartDate ? form.value.chargeStartDate.toISOString().split('T')[0] : null;

  return produce(payload, (draft) => {
    draft.facilityChargeStartDateMap = {
      ...draft.facilityChargeStartDateMap,
      [facilityId]: dateValue,
    };
  });
}
