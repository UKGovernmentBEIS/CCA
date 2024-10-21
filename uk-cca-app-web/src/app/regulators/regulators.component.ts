import { ChangeDetectionStrategy, Component } from '@angular/core';

import { TabLazyDirective, TabsComponent } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';

import { ExternalContactsComponent } from './external-contacts-tab/external-contacts.component';
import { RegulatorsUsersComponent } from './regulators-users-tab/regulators-users.component';
import { SiteContactsComponent } from './site-contacts-tab/site-contacts.component';

@Component({
  selector: 'cca-regulators',
  templateUrl: './regulators.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    RegulatorsUsersComponent,
    ExternalContactsComponent,
    SiteContactsComponent,
  ],
})
export class RegulatorsComponent {}
