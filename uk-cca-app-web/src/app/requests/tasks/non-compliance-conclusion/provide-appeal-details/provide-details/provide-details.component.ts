import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent, UuidFilePair, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';

import { ProvideAppealDetailsState, ProvideAppealDetailsStore } from '../+state';
import {
  PROVIDE_APPEAL_DETAILS_FORM,
  ProvideAppealDetailsFormModel,
  ProvideAppealDetailsFormProvider,
} from './provide-details-form.provider';

@Component({
  selector: 'cca-provide-appeal-details',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    DateInputComponent,
    MultipleFileInputComponent,
    TextareaComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ProvideAppealDetailsFormProvider],
  templateUrl: './provide-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideAppealDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly provideAppealDetailsStore = inject(ProvideAppealDetailsStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = inject<ProvideAppealDetailsFormModel>(PROVIDE_APPEAL_DETAILS_FORM);

  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);
  protected readonly downloadUrl = computed(() => generateDownloadUrl(this.taskId()?.toString()));
  protected readonly today = new Date();

  onSubmit() {
    const filesValue = this.form.value.files;
    const files = Array.isArray(filesValue) ? filesValue : filesValue ? [filesValue] : [];
    const uuidFilePairs: UuidFilePair[] = files.map((file) => ({
      uuid: file.uuid,
      file: file.file,
    }));

    const statePayload: ProvideAppealDetailsState = {
      appealDetails: {
        registrationDate: new DatePipe('en-GB').transform(this.form.value.registrationDate, 'yyyy-MM-dd'),
        files: fileUtils.toUUIDs(uuidFilePairs),
        comments: this.form.value.comments || undefined,
      },
      attachments: fileUtils.toAttachments(uuidFilePairs),
    };

    this.provideAppealDetailsStore.setState(statePayload);
    this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
  }
}
