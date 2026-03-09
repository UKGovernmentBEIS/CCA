import { produce } from 'immer';

import { UnderlyingAgreementVariationApplySavePayload } from 'cca-api';

export function updateVariableEnergyProductStatus(
  payload: UnderlyingAgreementVariationApplySavePayload,
  productName: string | null,
  facilityId: string,
  status: 'LIVE' | 'EXCLUDED',
) {
  return produce(payload, (draft) => {
    if (!productName) return;

    const facilityIndex = draft.facilities?.findIndex((facility) => facility.facilityId === facilityId);
    if (facilityIndex === -1) return;

    const facilityBaselineEnergyConsumption =
      draft.facilities[facilityIndex].cca3BaselineAndTargets?.facilityBaselineEnergyConsumption;

    const products = facilityBaselineEnergyConsumption?.variableEnergyConsumptionDataByProduct;
    if (!products) return;

    const product = products.find((item) => item.productName === productName);
    if (!product) return;

    product.productStatus = status;
  });
}
