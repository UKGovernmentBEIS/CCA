import { ConfigState, FeatureName } from '@core/config/config.state';
import { createSelector, StateSelector } from '@netz/common/store';

export const selectIsFeatureEnabled = (feature: FeatureName): StateSelector<ConfigState, boolean> =>
  createSelector((state) => state.features[feature]);

export const selectMeasurementId: StateSelector<ConfigState, string> = createSelector(
  (state) => state.analytics.measurementId,
);

export const selectPropertyId: StateSelector<ConfigState, string> = createSelector(
  (state) => state.analytics.propertyId,
);
