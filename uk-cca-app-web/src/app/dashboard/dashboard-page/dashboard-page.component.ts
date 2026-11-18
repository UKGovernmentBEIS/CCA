import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { TabLazyDirective, TabsComponent } from '@netz/govuk-components';

import { AssignedToMeComponent } from '../assigned-to-me/assigned-to-me.component';
import { AssignedToOthersComponent } from '../assigned-to-others/assigned-to-others.component';
import { UnassignedComponent } from '../unassigned/unassigned.component';

@Component({
  selector: 'cca-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    RouterModule,
    AssignedToMeComponent,
    AssignedToOthersComponent,
    UnassignedComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardPageComponent {
  private readonly authStore = inject(AuthStore);

  protected readonly role = computed(() => this.authStore.select(selectUserRoleType)());
}
