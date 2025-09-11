import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectIsLoggedIn, selectLoginStatus } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { BackToTopComponent, ServiceBannerComponent } from '@shared/components';
import { AuthService } from '@shared/services';

import { UserStateDTO } from 'cca-api';

interface ViewModel {
  isLoggedIn: boolean;
  status: UserStateDTO['status'];
}

@Component({
  selector: 'cca-landing-page',
  templateUrl: './landing-page.component.html',
  standalone: true,
  imports: [PageHeadingComponent, RouterLink, BackToTopComponent, NgTemplateOutlet, ServiceBannerComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingPageComponent {
  private readonly authStore = inject(AuthStore);
  protected readonly authService = inject(AuthService);

  protected readonly vm = computed<ViewModel>(() => ({
    isLoggedIn: this.authStore.select(selectIsLoggedIn)(),
    status: this.authStore.select(selectLoginStatus)(),
  }));
}
