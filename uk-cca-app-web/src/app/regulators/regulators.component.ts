import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { TabLazyDirective, TabsComponent } from '@netz/govuk-components';

import { ExternalContactsComponent } from './external-contacts-tab/external-contacts.component';
import { RegulatorsUsersComponent } from './regulators-users-tab/regulators-users.component';
import { SiteContactsComponent } from './site-contacts-tab/site-contacts.component';

@Component({
  selector: 'cca-regulators',
  template: `
    <netz-page-heading size="xl">Regulator users and contacts</netz-page-heading>

    <govuk-tabs>
      <ng-template govukTabLazy id="regulator-users" label="Regulator users">
        <cca-regulators-users />
      </ng-template>

      <ng-template govukTabLazy id="site-contacts" label="Site contacts">
        <cca-site-contacts />
      </ng-template>

      <ng-template govukTabLazy id="external-contacts" label="External contacts">
        <cca-external-contacts />
      </ng-template>
    </govuk-tabs>
  `,
  standalone: true,
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    RegulatorsUsersComponent,
    ExternalContactsComponent,
    SiteContactsComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorsComponent {}
