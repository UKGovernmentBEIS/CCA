import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { NotificationTemplatesService } from 'cca-api';

import { TemplateSearchComponent, TemplateSearchFetchFn } from '../template-search';

@Component({
  selector: 'cca-emails',
  template: `<cca-template-search [fetchFn]="fetchFn" templateType="email" fragment="emails" />`,
  imports: [TemplateSearchComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailsComponent {
  private readonly notificationTemplatesService = inject(NotificationTemplatesService);

  protected readonly fetchFn: TemplateSearchFetchFn = (page, pageSize, term) =>
    this.notificationTemplatesService.getCurrentUserNotificationTemplates(page, pageSize, [], term);
}
