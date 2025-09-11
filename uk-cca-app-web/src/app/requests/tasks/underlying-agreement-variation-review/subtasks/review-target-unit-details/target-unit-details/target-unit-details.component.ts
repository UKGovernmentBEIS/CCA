import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TARGET_UNIT_DETAILS_REVIEW_FORM,
  TargetUnitDetailsReviewFormModel,
  TargetUnitDetailsReviewFormProvider,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { operatorTypeOptions } from '@shared/pipes';
import { produce } from 'immer';

import { SectorAssociationSchemeService, SubsectorAssociationInfoDTO } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { applySaveActionSideEffects } from '../../../utils';

@Component({
  selector: 'cca-target-unit-details',
  templateUrl: './target-unit-details.component.html',
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
  providers: [TargetUnitDetailsReviewFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitDetailsComponent implements OnInit {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);

  private readonly sectorAssociationId = this.store.select(underlyingAgreementReviewQuery.selectSectorAssociationId);

  private readonly subsectors = signal<SubsectorAssociationInfoDTO[]>([]);

  protected readonly subsectorsOptions = computed(() =>
    this.subsectors().map((subsector) => ({
      text: subsector.name,
      value: subsector.id,
    })),
  );

  protected readonly form = inject<FormGroup<TargetUnitDetailsReviewFormModel>>(TARGET_UNIT_DETAILS_REVIEW_FORM);

  protected readonly operatorTypeOptions = operatorTypeOptions;

  ngOnInit() {
    if (this.sectorAssociationId()) {
      this.sectorAssociationSchemeService
        .getSectorAssociationSchemeBySectorAssociationId(this.sectorAssociationId())
        .subscribe((scheme) => {
          if (scheme.subsectorAssociations) {
            this.subsectors.set(scheme.subsectorAssociations);
          }
        });
    }
  }

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const selectedSubsectorAssociationName = this.subsectors().find(
      (subsector) => subsector.id === this.form.controls.subsectorAssociationId?.value,
    )?.name;

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.underlyingAgreementTargetUnitDetails.operatorName = this.form.getRawValue().operatorName;
      draft.underlyingAgreementTargetUnitDetails.operatorType = this.form.getRawValue().operatorType;

      draft.underlyingAgreementTargetUnitDetails.isCompanyRegistrationNumber =
        this.form.getRawValue().isCompanyRegistrationNumber;

      draft.underlyingAgreementTargetUnitDetails.companyRegistrationNumber =
        this.form.getRawValue().companyRegistrationNumber;

      draft.underlyingAgreementTargetUnitDetails.registrationNumberMissingReason =
        this.form.getRawValue().registrationNumberMissingReason;

      draft.underlyingAgreementTargetUnitDetails.subsectorAssociationId =
        this.form.getRawValue().subsectorAssociationId;

      draft.underlyingAgreementTargetUnitDetails.subsectorAssociationName = selectedSubsectorAssociationName;
    });

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}
