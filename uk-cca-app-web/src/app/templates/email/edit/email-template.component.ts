import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, first, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PendingButtonDirective } from '@shared/pending-button.directive';

import { GovukComponentsModule, GovukValidators } from 'govuk-components';

import { NotificationTemplateDTO, NotificationTemplatesService } from 'cca-api';

import { EmailTemplateDetailsTemplateComponent } from '../email-template-details-template.component';

@Component({
  selector: 'cca-email-template',
  templateUrl: './email-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    GovukComponentsModule,
    NgIf,
    AsyncPipe,
    PageHeadingComponent,
    ReactiveFormsModule,
    PendingButtonDirective,
    EmailTemplateDetailsTemplateComponent,
  ],
})
export class EmailTemplateComponent {
  emailTemplate$: Observable<NotificationTemplateDTO> = this.route.data.pipe(map(({ emailTemplate }) => emailTemplate));

  form$ = this.emailTemplate$.pipe(
    map((emailTemplate) =>
      this.fb.group({
        subject: [
          emailTemplate?.subject,
          [
            GovukValidators.required('Enter an email subject'),
            GovukValidators.maxLength(255, 'The email subject should not be more than 255 characters'),
          ],
        ],
        message: [
          emailTemplate?.text,
          [
            GovukValidators.required('Enter an email message'),
            GovukValidators.maxLength(10000, 'The email message should not be more than 10000 characters'),
          ],
        ],
      }),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly notificationTemplatesService: NotificationTemplatesService,
  ) {}

  onSubmit(): void {
    combineLatest([this.form$, this.emailTemplate$])
      .pipe(
        first(),
        tap(([form]) => {
          if (!form.valid) {
            this.displayErrorSummary$.next(true);
          }
        }),
        filter(([form]) => form.valid),
        switchMap(([form, emailTemplate]) =>
          form.dirty
            ? this.notificationTemplatesService.updateNotificationTemplate(emailTemplate.id, {
                subject: form.get('subject').value,
                text: form.get('message').value,
              })
            : of(null),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } }));
  }
}
