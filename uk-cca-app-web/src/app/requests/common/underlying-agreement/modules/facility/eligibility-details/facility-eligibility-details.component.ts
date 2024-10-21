import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  GovukSelectOption,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
} from '@netz/govuk-components';
import { AccountAddressInputComponent, FileInputComponent, WizardStepComponent } from '@shared/components';
import { TextInputComponent } from '@shared/components/text-input/text-input.component';
import { transformFilesToAttachments, transformFilesToUUIDsList } from '@shared/utils';

import { EligibilityDetailsAndAuthorisation } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';
import { AgreementTypeEnum, ApplicationReasonTypePipe, CaNameEnum } from '../../../pipes';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import {
  FACILITY_ELIGIBILITY_DETAILS_FORM,
  FacilityEligibilityDetailsFormProvider,
  FacilityEligibilityFormModel,
} from './facility-eligibility-details-form.provider';
@Component({
  selector: 'cca-facility-eligibility-details',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    ApplicationReasonTypePipe,
    AccountAddressInputComponent,
    TextInputComponent,
    SelectComponent,
    FileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './facility-eligibility-details.component.html',
  providers: [FacilityEligibilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityEligibilityDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityEligibilityFormModel>>(FACILITY_ELIGIBILITY_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  protected readonly agreementTypeOptions: GovukSelectOption<EligibilityDetailsAndAuthorisation['agreementType']>[] = [
    {
      value: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
      text: AgreementTypeEnum['ENVIRONMENTAL_PERMITTING_REGULATIONS'],
    },
    {
      value: 'ENERGY_INTENSIVE',
      text: AgreementTypeEnum['ENERGY_INTENSIVE'],
    },
  ];

  protected readonly caOptions: GovukSelectOption<EligibilityDetailsAndAuthorisation['regulatorName']>[] = [
    {
      value: 'ENVIRONMENT_AGENCY',
      text: CaNameEnum['ENVIRONMENT_AGENCY'],
    },
    {
      value: 'SCOTTISH_ENVIRONMENT_PROTECTION_AGENCY',
      text: CaNameEnum['SCOTTISH_ENVIRONMENT_PROTECTION_AGENCY'],
    },
    {
      value: 'DEPARTMENT_OF_AGRICULTURE_ENVIRONMENT_AND_RURAL_AFFAIRS',
      text: CaNameEnum['DEPARTMENT_OF_AGRICULTURE_ENVIRONMENT_AND_RURAL_AFFAIRS'],
    },
    {
      value: 'NATURAL_RESOURCES_WALES',
      text: CaNameEnum['NATURAL_RESOURCES_WALES'],
    },
    {
      value: 'OTHER',
      text: CaNameEnum['OTHER'],
    },
  ];

  private readonly agreementType: Signal<EligibilityDetailsAndAuthorisation['agreementType']> = toSignal(
    this.form.get('agreementType').valueChanges,
    {
      initialValue: this.form.get('agreementType').value,
    },
  );

  protected readonly isEnvironmental: Signal<boolean> = computed(() => {
    const agreementType = this.agreementType();

    if (agreementType === 'ENVIRONMENTAL_PERMITTING_REGULATIONS') {
      this.form.get('erpAuthorisationExists').enable();
      return true;
    } else {
      this.form.get('erpAuthorisationExists').disable();
      this.form.get('authorisationNumber').disable();
      this.form.get('regulatorName').disable();
      this.form.get('permitFile').disable();

      this.form.get('erpAuthorisationExists').reset();
      this.form.get('authorisationNumber').reset();
      this.form.get('regulatorName').reset();
      this.form.get('permitFile').reset();
      return false;
    }
  });

  private readonly erpAuthorisationExists: Signal<EligibilityDetailsAndAuthorisation['erpAuthorisationExists']> =
    toSignal(this.form.get('erpAuthorisationExists').valueChanges, {
      initialValue: this.form.get('erpAuthorisationExists').value,
    });

  protected readonly isAuthorisation: Signal<boolean> = computed(() => {
    if (this.erpAuthorisationExists()) {
      this.form.get('authorisationNumber').enable();
      this.form.get('regulatorName').enable();
      this.form.get('permitFile').enable();
      return true;
    } else {
      this.form.get('authorisationNumber').disable();
      this.form.get('regulatorName').disable();
      this.form.get('permitFile').disable();

      this.form.get('authorisationNumber').reset();
      this.form.get('regulatorName').reset();
      this.form.get('permitFile').reset();
      return false;
    }
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(underlyingAgreementQuery.selectPayload)();
    this.requestTaskStore.setPayload({ ...payload, currentFacilityId: this.facilityId });
    const attachments = transformFilesToAttachments([this.form.value.permitFile]);
    this.taskService
      .saveSubtask(FACILITIES_SUBTASK, FacilityWizardStep.ELIGIBILITY_DETAILS, this.activatedRoute, {
        facility: {
          facilityId: this.facilityId,
          eligibilityDetailsAndAuthorisation: {
            ...this.form.value,
            permitFile: transformFilesToUUIDsList(this.form.value.permitFile),
          },
        },
        attachments,
      })
      .subscribe();
  }
}
