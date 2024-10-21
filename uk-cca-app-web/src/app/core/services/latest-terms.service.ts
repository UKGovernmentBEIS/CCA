import { inject, Injectable } from '@angular/core';

import { Observable, of, tap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { LatestTermsStore } from '@core/store/latest-terms.store';
import { AuthStore } from '@netz/common/auth';

import { TermsAndConditionsService, TermsDTO } from 'cca-api';

@Injectable({ providedIn: 'root' })
export class LatestTermsService {
  lastestTermStore = inject(LatestTermsStore);
  configStore = inject(ConfigStore);
  authStore = inject(AuthStore);
  termsAndConditionsService = inject(TermsAndConditionsService);

  initLatestTerms(): Observable<TermsDTO> {
    const termsEnabled = this.configStore.select(selectIsFeatureEnabled('terms'));
    if (termsEnabled && this.authStore.state.isLoggedIn) {
      return this.termsAndConditionsService
        .getLatestTerms()
        .pipe(tap((lt) => this.lastestTermStore.setLatestTerms(lt)));
    }
    return of(null);
  }
}
