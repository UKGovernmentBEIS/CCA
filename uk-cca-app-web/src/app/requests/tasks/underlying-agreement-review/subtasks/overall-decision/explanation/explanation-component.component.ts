import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators, TextareaComponent } from '@netz/govuk-components';
import { OverallDecisionWizardStep } from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { OverallDecisionStore } from '../overall-decision.store';

@Component({
  selector: 'cca-explanation-component',
  standalone: true,
  template: `
    <cca-wizard-step
      [formGroup]="form"
      caption="Reject"
      heading="Explain why you are rejecting the application"
      (formSubmit)="submit()"
    >
      <div govuk-textarea formControlName="reason" hint="This will be included in the official notice."></div>
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
    ReturnToTaskOrActionPageComponent,
  ],
})
export class ExplanationComponentComponent {
  overallDecisionStore = inject(OverallDecisionStore);
  store = inject(RequestTaskStore);
  fb = inject(FormBuilder);
  taskService = inject(TaskService);
  router = inject(Router);
  route = inject(ActivatedRoute);
  determination = this.overallDecisionStore.determination;
  form = this.fb.group({
    reason: this.fb.control(this.determination.reason, GovukValidators.required('Enter a reason for rejecting.')),
  });
  submit() {
    this.overallDecisionStore.updateDetermination({ reason: this.form.value.reason });
    this.router.navigate(['../', OverallDecisionWizardStep.ADDITIONAL_INFO], {
      relativeTo: this.route,
      queryParamsHandling: 'preserve',
    });
  }
}
