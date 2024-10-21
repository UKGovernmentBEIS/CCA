import { inject, Injectable } from '@angular/core';

import { map, Observable, tap } from 'rxjs';

import { selectIsFeatureEnabled, selectMeasurementId, selectPropertyId } from '@core/config/config.selectors';
import { ConfigState, FeatureName } from '@core/config/config.state';
import { ConfigStore } from '@core/config/config.store';

import { UIConfigurationService } from 'cca-api';

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

  getMeasurementId(): string {
    return this.configStore.select(selectMeasurementId)();
  }

  getPropertyId(): string {
    return this.configStore.select(selectPropertyId)();
  }
}
