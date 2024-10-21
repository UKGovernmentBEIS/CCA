import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectIsLoggedIn, selectLoginStatus } from '@netz/common/auth';
import { DestroySubject } from '@netz/common/services';
import { GovukComponentsModule } from '@netz/govuk-components';
import { BackToTopComponent, PageHeadingComponent, ServiceBannerComponent } from '@shared/components';

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
  providers: [DestroySubject],
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
