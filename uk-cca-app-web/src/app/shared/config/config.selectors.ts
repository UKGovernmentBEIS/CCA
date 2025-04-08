import { createSelector, StateSelector } from '@netz/common/store';

import { ConfigState, FeatureName } from './config.state';

export const selectIsFeatureEnabled = (feature: FeatureName): StateSelector<ConfigState, boolean> =>
  createSelector((state) => state.features[feature]);

export const selectGtmContainerId: StateSelector<ConfigState, string> = createSelector(
  (state) => state.analytics.gtmContainerId,
);

export const selectSubsistenceFeesRunTriggerDate: StateSelector<ConfigState, string> = createSelector(
  (state) => state.subsistenceFeesRunTriggerDate,
);
