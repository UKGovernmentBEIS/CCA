export const FEATURES = [
  'terms',
  'unaHideNotifyOperator',
  'subsistenceFeesHideMenu',
  'hideNotes',
  'hideFacilityAudit',
  'unaVariationHideStartTask',
] as const;

export type FeatureName = (typeof FEATURES)[number];
export type FeaturesConfig = Partial<Record<FeatureName, boolean>>;

export interface ConfigState {
  features?: FeaturesConfig;
  analytics?: {
    gtmContainerId: string;
  };
  keycloakServerUrl?: string;
  subsistenceFeesRunTriggerDate?: string;
  underlyingAgreementSchemeParticipationFlagCutOffDate?: string;
}

export const initialState: ConfigState = {
  features: {
    unaHideNotifyOperator: false,
    subsistenceFeesHideMenu: false,
    hideNotes: false,
    hideFacilityAudit: false,
    unaVariationHideStartTask: false,
  },
  analytics: {
    gtmContainerId: '',
  },
  subsistenceFeesRunTriggerDate: '',
  underlyingAgreementSchemeParticipationFlagCutOffDate: '',
};
