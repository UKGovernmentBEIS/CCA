import { ProductVariableEnergyConsumptionData } from 'cca-api';

import { ProductFormGroup } from '../../../../tasks/underlying-agreement-variation/subtasks/manage-facilities/facility/baseline-energy-consumption/add-product/add-product-form.provider';

export function mapToProductVariableEnergyConsumptionData(
  formValue: Partial<ReturnType<ProductFormGroup['getRawValue']>>,
  previousProduct?: ProductVariableEnergyConsumptionData,
): ProductVariableEnergyConsumptionData {
  const { productName, baselineYear, baselineVariableEnergy, baselineThroughput, throughputUnit } = formValue;

  return {
    productName: productName ?? previousProduct?.productName ?? '',
    productStatus: previousProduct?.productStatus ?? 'NEW',
    baselineYear: baselineYear ?? previousProduct?.baselineYear,
    energy: String(resolveNumber(Number(baselineVariableEnergy), Number(previousProduct?.energy))),
    throughput: String(resolveNumber(Number(baselineThroughput), Number(previousProduct?.throughput))),
    throughputUnit: throughputUnit ?? previousProduct?.throughputUnit ?? '',
  };
}

function resolveNumber(current: number, previous: number): number {
  if (Number.isFinite(current) && current > 0) return current;
  if (Number.isFinite(previous) && previous > 0) return previous;
  return 0;
}
