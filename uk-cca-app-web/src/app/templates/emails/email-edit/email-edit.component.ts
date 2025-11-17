import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { BusinessError } from '@error/business-error/business-error';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, GovukValidators, TextareaComponent, TextInputComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { NotificationTemplatesService } from 'cca-api';

import { toEmailTemplateSummary } from '../email-template-summary';

@Component({
  selector: 'cca-email-edit',
  templateUrl: './email-edit.component.html',
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    SummaryComponent,
    TextInputComponent,
    TextareaComponent,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailEditComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly notificationTemplatesService = inject(NotificationTemplatesService);

  protected readonly emailTemplate = toSignal(
    this.notificationTemplatesService.getNotificationTemplateById(this.activatedRoute.snapshot.params.templateId).pipe(
      catchError(() => {
        throw new BusinessError('Could not get template details');
      }),
    ),
  );

  readonly data = computed(() => toEmailTemplateSummary(this.emailTemplate(), true));

  readonly form = computed(
    () =>
      new FormGroup({
        subject: new FormControl(
          this.emailTemplate()?.subject ?? null,
          GovukValidators.required('Enter an email subject'),
        ),
        message: new FormControl(
          this.emailTemplate()?.text ?? null,
          GovukValidators.required('Enter an email message'),
        ),
      }),
  );

  onSubmit() {
    this.notificationTemplatesService
      .updateNotificationTemplate(this.emailTemplate().id, {
        subject: this.form().value.subject,
        text: this.form().value.message,
      })
      .pipe(
        catchError(() => {
          throw new BusinessError('Could not update template');
        }),
      )
      .subscribe(() =>
        this.router.navigate(['..'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
          queryParams: {},
          state: { notification: true },
        }),
      );
  }
}
