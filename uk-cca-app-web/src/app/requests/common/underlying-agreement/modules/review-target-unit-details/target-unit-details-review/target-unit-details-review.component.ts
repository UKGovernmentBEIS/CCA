import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
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
import { operatorTypeOptions } from '@shared/pipes';
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
  private readonly store = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);

  protected readonly form = inject<FormGroup<TargetUnitDetailsReviewFormModel>>(TARGET_UNIT_DETAILS_REVIEW_FORM);

  private readonly sectorAssociationId = this.store.select(underlyingAgreementReviewQuery.selectSectorAssociationId);
  private readonly subsectors = signal<SubsectorAssociationSchemeInfoDTO[]>([]);

  protected readonly subsectorsOptions = computed(() =>
    this.subsectors().map((subsector) => ({
      text: subsector.subsectorAssociation.name,
      value: subsector.id,
    })),
  );

  protected readonly operatorTypeOptions = operatorTypeOptions;

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
    const selectedSubsectorAssociationName = computed(
      () =>
        this.subsectors().find((ssa) => ssa.id === this.form.value.subsectorAssociationId)?.subsectorAssociation?.name,
    );

    const formData = { ...this.form.getRawValue(), subsectorAssociationName: selectedSubsectorAssociationName() };

    this.taskService
      .saveSubtask(
        REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
        ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        this.activatedRoute,
        formData,
      )
      .subscribe();
  }
}
