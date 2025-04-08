import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { NotificationBannerComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { BaselineAndTargetPeriodsSubtasks, BaseLineAndTargetsStep } from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import {
  BASELINE_EXISTS_FORM,
  BaselineExistsFormModel,
  BaselineExistsFormProvider,
} from './baseline-exists-form.provider';

@Component({
  selector: 'cca-baseline-exists',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioOptionComponent,
    RadioComponent,
    ReturnToTaskOrActionPageComponent,
    NotificationBannerComponent,
  ],
  templateUrl: './baseline-exists.component.html',
  providers: [BaselineExistsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineExistsComponent {
  protected readonly form = inject<BaselineExistsFormModel>(BASELINE_EXISTS_FORM);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  showNotificationBanner = this.router.url.includes('underlying-agreement-application');

  onSubmit() {
    this.taskService
      .saveSubtask(
        BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
        BaseLineAndTargetsStep.BASELINE_EXISTS,
        this.activatedRoute,
        this.form.value,
      )
      .subscribe();
  }
}
