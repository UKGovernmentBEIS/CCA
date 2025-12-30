import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { BusinessError } from '@error/business-error/business-error';
import { PageHeadingComponent } from '@netz/common/components';
import { NotificationBannerComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { DocumentTemplatesService } from 'cca-api';

import { toDocumentTemplateSummary } from '../document-template-summary';

@Component({
  selector: 'cca-document-view',
  template: `
    @if (documentTemplate(); as documentTemplate) {
      @if (notification()) {
        <govuk-notification-banner type="success">
          <h1 class="govuk-notification-banner__heading">Template updated</h1>
        </govuk-notification-banner>
      }

      <netz-page-heading>{{ documentTemplate.name }}</netz-page-heading>
      <cca-summary [data]="data()" />
    }
  `,
  imports: [PageHeadingComponent, SummaryComponent, NotificationBannerComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentViewComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly documentTemplatesService = inject(DocumentTemplatesService);

  protected readonly notification = signal(this.router.currentNavigation()?.extras.state?.notification);

  protected readonly documentTemplate = toSignal(
    this.documentTemplatesService.getDocumentTemplateById(this.activatedRoute.snapshot.params.templateId).pipe(
      catchError(() => {
        throw new BusinessError('Could not get template details');
      }),
    ),
  );

  readonly data = computed(() => toDocumentTemplateSummary(this.documentTemplate(), false));
}
