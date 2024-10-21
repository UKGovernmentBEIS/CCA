import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { ButtonDirective } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

@Component({
  selector: 'cca-una-submit-action',
  standalone: true,
  imports: [ButtonDirective, PageHeadingComponent, PendingButtonDirective, ReturnToTaskOrActionPageComponent],
  template: `
    <cca-page-heading size="xl"> Submit to regulator </cca-page-heading>

    <p class="govuk-body">Your application will be sent directly to your Regulator (Environment Agency).</p>
    <p class="govuk-body">
      By selecting 'Confirm and send' you confirm that the information in your application is correct to the best of
      your knowledge.
    </p>

    <div class="govuk-button-group">
      <button type="button" ccaPendingButton (click)="submit()" govukButton>Confirm and send</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementSubmitActionComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksService = inject(TaskService);

  submit() {
    this.tasksService
      .submit()
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
