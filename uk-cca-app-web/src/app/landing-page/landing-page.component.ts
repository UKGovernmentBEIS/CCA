import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectIsLoggedIn, selectLoginStatus } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { GovukComponentsModule } from '@netz/govuk-components';
import { BackToTopComponent, ServiceBannerComponent } from '@shared/components';
import { AuthService } from '@shared/services';

import { UserStateDTO } from 'cca-api';

interface ViewModel {
  isLoggedIn: boolean;
  status: UserStateDTO['status'];
}

@Component({
  selector: 'cca-landing-page',
  standalone: true,
  templateUrl: './landing-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    PageHeadingComponent,
    RouterLink,
    BackToTopComponent,
    NgTemplateOutlet,
    GovukComponentsModule,
    ServiceBannerComponent,
  ],
})
export class LandingPageComponent {
  private readonly authStore = inject(AuthStore);
  protected readonly authService = inject(AuthService);

  vm = computed<ViewModel>(() => ({
    isLoggedIn: this.authStore.select(selectIsLoggedIn)(),
    status: this.authStore.select(selectLoginStatus)(),
  }));
}
