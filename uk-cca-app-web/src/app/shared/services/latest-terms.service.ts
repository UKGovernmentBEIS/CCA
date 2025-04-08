import { inject, Injectable } from '@angular/core';

import { Observable, of, tap } from 'rxjs';

import { AuthStore } from '@netz/common/auth';

import { TermsAndConditionsService, TermsDTO } from 'cca-api';

import { selectIsFeatureEnabled } from '../config/config.selectors';
import { ConfigStore } from '../config/config.store';
import { LatestTermsStore } from './latest-terms.store';

@Injectable({ providedIn: 'root' })
export class LatestTermsService {
  private readonly lastestTermStore = inject(LatestTermsStore);
  private readonly configStore = inject(ConfigStore);
  private readonly authStore = inject(AuthStore);
  private readonly termsAndConditionsService = inject(TermsAndConditionsService);

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
