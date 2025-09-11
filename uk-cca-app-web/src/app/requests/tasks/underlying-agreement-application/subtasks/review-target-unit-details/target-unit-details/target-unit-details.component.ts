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
  TARGET_UNIT_DETAILS_SUBMIT_FORM,
  TargetUnitDetailsSubmitFormModel,
  TargetUnitDetailsSubmitFormProvider,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { operatorTypeOptions, transformOperatorType } from '@shared/pipes';
import { produce } from 'immer';

import {
  SectorAssociationSchemeService,
  SubsectorAssociationInfoDTO,
  UnderlyingAgreementSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

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
  providers: [TargetUnitDetailsSubmitFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitDetailsComponent implements OnInit {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);

  private readonly sectorAssociationId = this.store.select(underlyingAgreementReviewQuery.selectSectorAssociationId);

  private readonly subsectors = signal<SubsectorAssociationInfoDTO[]>([]);

  protected readonly subsectorsOptions = computed(() =>
    this.subsectors().map((subsector) => ({
      text: subsector.name,
      value: subsector.id,
    })),
  );

  protected readonly transformOperatorType = transformOperatorType;
  protected readonly operatorTypeOptions = operatorTypeOptions;

  protected readonly form = inject<FormGroup<TargetUnitDetailsSubmitFormModel>>(TARGET_UNIT_DETAILS_SUBMIT_FORM);

  ngOnInit() {
    if (this.sectorAssociationId()) {
      this.sectorAssociationSchemeService
        .getSectorAssociationSchemeBySectorAssociationId(this.sectorAssociationId())
        .subscribe((scheme) => {
          if (scheme.subsectorAssociations) this.subsectors.set(scheme.subsectorAssociations);
        });
    }
  }

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.underlyingAgreementTargetUnitDetails = {
        ...draft.underlyingAgreementTargetUnitDetails,
        ...this.form.getRawValue(),
        subsectorAssociationName: this.subsectors().find(
          (subsector) => subsector.id === this.form.get('subsectorAssociationId')?.value,
        )?.name,
      };
    });

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}
