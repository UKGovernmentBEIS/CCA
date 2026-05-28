import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';
import { FileUploadEvent, MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
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
    MultipleFileInputComponent,
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
  protected readonly downloadUrl = computed(() => generateDownloadUrl(this.requestTaskId()?.toString()));

  onSubmit() {
    const file = this.getUploadedFile();
    const appealOutcome: AppealOutcome = {
      tribunalDecision: this.form.controls.tribunalDecision.value,
      appealOutcomeDate: new DatePipe('en-GB').transform(this.form.controls.appealOutcomeDate.value, 'yyyy-MM-dd'),
      file: file?.uuid ?? null,
      comments: this.form.controls.comments.value ?? null,
    };

    const dto = createSaveActionDTO(this.requestTaskId(), appealOutcome);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }

  private getUploadedFile(): FileUploadEvent {
    const value = this.form.controls.file.value;
    const files = Array.isArray(value) ? value : value ? [value] : [];

    return files[0];
  }
}
