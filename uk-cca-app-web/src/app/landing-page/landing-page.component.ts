import { AsyncPipe, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { loginEnabled } from '@core/util/user-status-util';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import { RelatedContentComponent } from '@shared/components/related-content/related-content.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { KeycloakProfile } from 'keycloak-js';

import { GovukComponentsModule } from 'govuk-components';

import { UserStateDTO } from 'cca-api';

interface ViewModel {
  isLoggedIn: boolean;
  userProfile: KeycloakProfile;
  roleType: UserStateDTO['roleType'];
  status: UserStateDTO['status'];
  installationEnabled: boolean;
}

@Component({
  selector: 'cca-landing-page',
  standalone: true,
  templateUrl: './landing-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
  imports: [
    NgIf,
    AsyncPipe,
    PageHeadingComponent,
    RouterLink,
    RelatedContentComponent,
    BackToTopComponent,
    NgTemplateOutlet,
    NgSwitch,
    NgSwitchCase,
    NgSwitchDefault,
    GovukComponentsModule,
  ],
})
export class LandingPageComponent {
  vm$: Observable<ViewModel> = this.authStore.pipe(
    map(({ userState, userProfile, isLoggedIn }) => ({
      userProfile,
      isLoggedIn,
      roleType: userState?.roleType,
      status: userState?.status,
      installationEnabled: loginEnabled(userState),
    })),
  );

  constructor(
    private readonly authStore: AuthStore,
    public readonly authService: AuthService,
  ) {}
}
