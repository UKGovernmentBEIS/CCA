import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { BusinessError } from '@error/business-error/business-error';
import { PageHeadingComponent } from '@netz/common/components';
import { NotificationBannerComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { NotificationTemplatesService } from 'cca-api';

import { toEmailTemplateSummary } from '../email-template-summary';

@Component({
  selector: 'cca-email-view',
  template: `
    @if (emailTemplate(); as emailTemplate) {
      @if (notification()) {
        <govuk-notification-banner type="success">
          <h1 class="govuk-notification-banner__heading">Template updated</h1>
        </govuk-notification-banner>
      }

      <netz-page-heading>{{ emailTemplate.name }}</netz-page-heading>
      <cca-summary [data]="data()" />
    }
  `,
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, NotificationBannerComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailViewComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly notificationTemplatesService = inject(NotificationTemplatesService);

  protected readonly notification = signal(this.router.getCurrentNavigation()?.extras.state?.notification);

  protected readonly emailTemplate = toSignal(
    this.notificationTemplatesService.getNotificationTemplateById(this.activatedRoute.snapshot.params.templateId).pipe(
      catchError(() => {
        throw new BusinessError('Could not get template details');
      }),
    ),
  );

  readonly data = computed(() => toEmailTemplateSummary(this.emailTemplate(), false));
}
