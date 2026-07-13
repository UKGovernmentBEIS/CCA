import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { createSaveActionDTO } from '../transform';
import { AppealOutcome } from '../types';
import {
  APPEAL_OUTCOME_FORM,
  AppealOutcomeFormModel,
  AppealOutcomeFormProvider,
} from './provide-details-form.provider';

@Component({
  selector: 'cca-appeal-outcome-provide-details',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    RadioComponent,
    RadioOptionComponent,
    DateInputComponent,
    FileInputComponent,
    TextareaComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [AppealOutcomeFormProvider],
  templateUrl: './provide-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<AppealOutcomeFormModel>(APPEAL_OUTCOME_FORM);
  protected readonly today = new Date();

  private readonly requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);
  protected readonly getDownloadUrl = (uuid: string) =>
    `${generateDownloadUrl(this.requestTaskId()?.toString())}${uuid}`;

  onSubmit() {
    const appealOutcome: AppealOutcome = {
      tribunalDecision: this.form.controls.tribunalDecision.value,
      appealOutcomeDate: new DatePipe('en-GB').transform(this.form.controls.appealOutcomeDate.value, 'yyyy-MM-dd'),
      file: this.form.controls.file.value?.uuid ?? null,
      comments: this.form.controls.comments.value ?? null,
    };

    const dto = createSaveActionDTO(this.requestTaskId(), appealOutcome);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
