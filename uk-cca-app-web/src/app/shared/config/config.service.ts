import { inject, Injectable } from '@angular/core';

import { map, Observable, tap } from 'rxjs';

import { UIConfigurationService } from 'cca-api';

import {
  selectGtmContainerId,
  selectIsFeatureEnabled,
  selectSubsistenceFeesRunTriggerDate,
  selectUnderlyingAgreementSchemeParticipationFlagCutOffDate,
} from './config.selectors';
import { ConfigState, FeatureName } from './config.state';
import { ConfigStore } from './config.store';

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private readonly configStore = inject(ConfigStore);
  private readonly configurationService = inject(UIConfigurationService);

  initConfigState(): Observable<ConfigState> {
    return this.configurationService.getUIFlags().pipe(
      tap((props) => this.configStore.setState({ ...props } as ConfigState)),
      map(() => this.configStore.state),
    );
  }

  isFeatureEnabled(feature: FeatureName): boolean {
    return this.configStore.select(selectIsFeatureEnabled(feature))();
  }

  getGtmContainerId(): string {
    return this.configStore.select(selectGtmContainerId)();
  }

  getSubsistenceFeesRunTriggerDate() {
    return this.configStore.select(selectSubsistenceFeesRunTriggerDate)();
  }

  getUnderlyingAgreementSchemeParticipationFlagCutOffDate() {
    return this.configStore.select(selectUnderlyingAgreementSchemeParticipationFlagCutOffDate)();
  }
}
