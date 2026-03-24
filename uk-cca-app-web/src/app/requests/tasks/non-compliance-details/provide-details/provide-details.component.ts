import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DateInputComponent,
  DetailsComponent,
  GovukSelectOption,
  SelectComponent,
  TextareaComponent,
} from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { NonComplianceDetails, RequestTaskActionPayload } from 'cca-api';

import { isNonComplianceWizardCompleted } from '../non-compliance-details.guard';
import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';
import { NON_COMPLIANCE_TYPE_LABELS } from '../non-compliance-type-labels';
import { NON_COMPLIANCE_DETAILS_SUBTASK, NonComplianceDetailsPayload } from '../types';
import {
  PROVIDE_DETAILS_FORM,
  ProvideDetailsFormModel,
  ProvideDetailsFormProvider,
} from './provide-details-form.provider';

@Component({
  selector: 'cca-provide-details',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    SelectComponent,
    DateInputComponent,
    TextareaComponent,
    DetailsComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ProvideDetailsFormProvider],
  templateUrl: './provide-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  protected readonly today = new Date();

  protected readonly form = inject<ProvideDetailsFormModel>(PROVIDE_DETAILS_FORM);

  protected readonly nonComplianceTypeOptions: GovukSelectOption<NonComplianceDetails['nonComplianceType'] | null>[] = [
    { value: null, text: '' },
    ...Object.entries(NON_COMPLIANCE_TYPE_LABELS).map(([value, text]) => ({
      value: value as NonComplianceDetails['nonComplianceType'],
      text,
    })),
  ];

  onSubmit() {
    const payload = this.requestTaskStore.select(
      nonComplianceDetailsQuery.selectPayload,
    )() as NonComplianceDetailsPayload;
    const nonComplianceType = this.form.value.nonComplianceType as NonComplianceDetails['nonComplianceType'];

    const nonComplianceDetails: NonComplianceDetails = {
      ...payload.nonComplianceDetails,
      nonComplianceType,
      nonCompliantDate: this.convertToDateString(this.form.value.nonCompliantDate),
      compliantDate: this.convertToDateString(this.form.value.compliantDate),
      comment: this.form.value.comment,
    };

    const currentSectionsCompleted = this.requestTaskStore.select(nonComplianceDetailsQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[NON_COMPLIANCE_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = {
      requestTaskId,
      requestTaskActionType: 'NON_COMPLIANCE_DETAILS_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_DETAILS_SAVE_PAYLOAD',
        nonComplianceDetails,
        sectionsCompleted,
      } as RequestTaskActionPayload,
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      if (isNonComplianceWizardCompleted(nonComplianceDetails)) {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['..', 'choose-relevant-workflows'], { relativeTo: this.activatedRoute });
      }
    });
  }

  private convertToDateString(date: Date | string): string {
    if (!date) {
      return null;
    }

    return typeof date === 'string' ? date : date.toISOString();
  }
}
