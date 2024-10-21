import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { OverallDecisionWizardStep } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { OverallDecisionStore } from '../overall-decision.store';
import { ADDITIONAL_INFO_FORM, AdditionalInfoFormModel, provideAdditionalInfo } from './additional-info.provider';

@Component({
  selector: 'cca-explanation-component',
  standalone: true,
  template: `
    <cca-wizard-step
      [formGroup]="form"
      [caption]="caption()"
      heading="Provide any additional information here to support your decision (optional)"
      (formSubmit)="submit()"
    >
      <div govuk-textarea formControlName="additionalInfo" hint="This will be included in the official notice."></div>
      <cca-multiple-file-input [baseDownloadUrl]="downloadUrl" formControlName="files"></cca-multiple-file-input>
    </cca-wizard-step>
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    WizardStepComponent,
    PageHeadingComponent,
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [provideAdditionalInfo()],
})
export class AdditionalInfoComponent {
  fb = inject(FormBuilder);
  overallDecisionStore = inject(OverallDecisionStore);
  store = inject(RequestTaskStore);

  taskService = inject(TaskService);
  router = inject(Router);
  route = inject(ActivatedRoute);

  form = inject<AdditionalInfoFormModel>(ADDITIONAL_INFO_FORM);

  caption = computed(() => (this.overallDecisionStore.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject'));
  downloadUrl = generateDownloadUrl(this.store.select(requestTaskQuery.selectRequestTaskId)().toString());
  submit() {
    const files = this.form.value.files.map((f) => f.uuid);
    this.overallDecisionStore.updateDetermination(
      { additionalInformation: this.form.value.additionalInfo, files },
      this.form.value.files,
    );

    this.router.navigate(['../', OverallDecisionWizardStep.CHECK_ANSWERS], {
      relativeTo: this.route,
      queryParamsHandling: 'preserve',
    });
  }
}
