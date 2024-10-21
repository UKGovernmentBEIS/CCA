import { Facility } from 'cca-api';

export const isFacilityWizardCompleted = (facility: Facility) => {
  return (
    !!facility &&
    !!facility?.facilityDetails &&
    !!facility?.facilityContact &&
    !!facility?.eligibilityDetailsAndAuthorisation &&
    !!facility?.facilityExtent &&
    !!facility?.apply70Rule
  );
};
