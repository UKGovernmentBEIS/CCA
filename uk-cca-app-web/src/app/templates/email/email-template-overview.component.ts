import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { GovukComponentsModule } from 'govuk-components';

import { NotificationTemplateDTO } from 'cca-api';

import { EmailTemplateDetailsTemplateComponent } from './email-template-details-template.component';

@Component({
  selector: 'cca-email-template-overview',
  templateUrl: './email-template-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    GovukComponentsModule,
    AsyncPipe,
    PageHeadingComponent,
    EmailTemplateDetailsTemplateComponent,
    RouterLink,
    NgIf,
  ],
})
export class EmailTemplateOverviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  emailTemplate$: Observable<NotificationTemplateDTO> = this.route.data.pipe(map(({ emailTemplate }) => emailTemplate));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}
}
