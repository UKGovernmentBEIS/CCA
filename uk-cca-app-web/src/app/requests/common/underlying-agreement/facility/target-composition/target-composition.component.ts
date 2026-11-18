import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import {
  FileInputComponent,
  TextInputComponent as CcaTextInputComponent,
  WizardStepComponent,
} from '@shared/components';
import { MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP, transformMeasurementType } from '@shared/pipes';
import { generateDownloadUrl } from '@shared/utils';

import { TargetComposition } from 'cca-api';

import { underlyingAgreementQuery } from '../../+state';
import { FacilityTargetCompositionFormModel } from '../../target-periods';
import { FacilityTargetCompositionSubmitEvent } from '../types';
import { TARGET_COMPOSITION_FORM, TargetCompositionFormProvider } from './target-composition-form.provider';

@Component({
  selector: 'cca-common-facility-target-composition',
  templateUrl: './target-composition.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    SelectComponent,
    CcaTextInputComponent,
    RouterLink,
  ],
  providers: [TargetCompositionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommonFacilityTargetCompositionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityTargetCompositionFormModel>(TARGET_COMPOSITION_FORM);

  protected readonly submitted = output<FacilityTargetCompositionSubmitEvent>();

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly transformMeasurementType = transformMeasurementType;

  protected getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly measurementTypeOptions: GovukSelectOption<TargetComposition['measurementType']>[] = [
    {
      value: 'ENERGY_KWH',
      text: MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP.ENERGY_KWH,
    },
    {
      value: 'ENERGY_MWH',
      text: MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP.ENERGY_MWH,
    },
    {
      value: 'ENERGY_GJ',
      text: MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP.ENERGY_GJ,
    },
    {
      value: 'CARBON_KG',
      text: MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP.CARBON_KG,
    },
    {
      value: 'CARBON_TONNE',
      text: MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP.CARBON_TONNE,
    },
  ];

  onSubmit() {
    this.submitted.emit({ form: this.form, facilityId: this.facilityId });
  }
}
