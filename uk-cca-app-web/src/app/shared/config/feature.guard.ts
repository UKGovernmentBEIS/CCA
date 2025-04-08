import { inject } from '@angular/core';

import { ConfigService } from './config.service';
import { FeatureName } from './config.state';

export function isFeatureEnabled(feature: FeatureName) {
  return () => inject(ConfigService).isFeatureEnabled(feature);
}
