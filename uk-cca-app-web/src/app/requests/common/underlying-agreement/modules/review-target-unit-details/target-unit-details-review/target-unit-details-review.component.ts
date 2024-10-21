import { ChangeDetectionStrategy, Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';
import { operatorTypeOptions, OperatorTypePipe } from '@shared/pipes/operator-type.pipe';
import { existingControlContainer } from '@shared/providers';

import { SectorAssociationSchemeService, SubsectorAssociationSchemeInfoDTO } from 'cca-api';

import { underlyingAgreementReviewQuery } from '../../../+state';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
} from '../../../underlying-agreement.types';
import {
  TARGET_UNIT_DETAILS_REVIEW_FORM,
  TargetUnitDetailsReviewFormModel,
  TargetUnitDetailsReviewFormProvider,
} from './target-unit-details-review-form.provider';

@Component({
  selector: 'cca-target-unit-details-review',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    OperatorTypePipe,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    SelectComponent,
    ConditionalContentDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './target-unit-details-review.component.html',
  providers: [TargetUnitDetailsReviewFormProvider],
  viewProviders: [existingControlContainer],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitDetailsReviewComponent implements OnInit {
  protected readonly form = inject<FormGroup<TargetUnitDetailsReviewFormModel>>(TARGET_UNIT_DETAILS_REVIEW_FORM);
  private readonly store = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  private readonly sectorAssociationId = this.store.select(underlyingAgreementReviewQuery.selectSectorAssociationId);
  private readonly subsectors = signal<SubsectorAssociationSchemeInfoDTO[]>([]);

  readonly isCompanyRegistrationNumberValue = toSignal(this.form.controls.isCompanyRegistrationNumber.valueChanges, {
    initialValue: this.form.value.isCompanyRegistrationNumber,
  });

  constructor() {
    effect(() => {
      if (this.isCompanyRegistrationNumberValue()) {
        this.form.get('companyRegistrationNumber').enable();
        this.form.get('registrationNumberMissingReason').disable();
        this.form.get('registrationNumberMissingReason').reset();
      } else {
        this.form.get('registrationNumberMissingReason').enable();
        this.form.get('companyRegistrationNumber').disable();
        this.form.get('companyRegistrationNumber').reset();
      }
    });
  }

  subsectorsOptions = computed(() =>
    this.subsectors().map((subsector) => ({
      text: subsector.subsectorAssociation.name,
      value: subsector.id,
    })),
  );
  operatorTypeOptions = operatorTypeOptions;

  ngOnInit(): void {
    if (this.sectorAssociationId()) {
      this.sectorAssociationSchemeService
        .getSectorAssociationSchemeBySectorAssociationId(this.sectorAssociationId())
        .subscribe((scheme) => {
          if (scheme.subsectorAssociationSchemes) {
            this.subsectors.set(scheme.subsectorAssociationSchemes);
          }
        });
    }
  }

  onSubmit() {
    this.taskService
      .saveSubtask(
        REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
        ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        this.activatedRoute,
        this.form.getRawValue(),
      )
      .subscribe();
  }
}
