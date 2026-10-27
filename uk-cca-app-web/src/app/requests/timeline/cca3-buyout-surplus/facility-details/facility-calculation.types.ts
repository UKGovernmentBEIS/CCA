export type FacilityCalculationScenario =
  | 'primary-surplus'
  | 'primary-buyout'
  | 'secondary-surplus'
  | 'secondary-target-met-refund'
  | 'secondary-buyout-after-buyout'
  | 'secondary-buyout-after-surplus'
  | 'secondary-target-not-met-refund';

/** Financial layouts; the displayed Type comes from the buy-out's primaryRulesApplied flag. */
export type FacilityCalculationPresentation =
  | { scenario: 'primary-surplus' | 'secondary-surplus' | 'secondary-target-met-refund' }
  | { scenario: 'primary-buyout'; showTransaction: boolean }
  | {
      scenario: 'secondary-buyout-after-buyout' | 'secondary-buyout-after-surplus' | 'secondary-target-not-met-refund';
      grossBuyoutFee: string | null;
    };
