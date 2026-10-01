import { InjectionToken, Provider } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { createZeroEnergyValidatorFn, tprFormQuery } from '@requests/common';
import { CCAGovukValidators } from '@shared/validators';

import { PerformanceDataFacilityProductVariableEnergyData, ProductVariableEnergyConsumptionData } from 'cca-api';

type ProductRowForm = FormGroup<{
  productName: FormControl<string>;
  baselineYear: FormControl<number>;
  energy: FormControl<string>;
  targetImprovement: FormControl<number>;
  actualThroughput: FormControl<string>;
  throughputUnit: FormControl<string>;
  adjustedThroughput: FormControl<number>;
  targetEnergy: FormControl<number>;
}>;

export type ProductsArrayForm = FormGroup<{
  products: FormArray<ProductRowForm>;
}>;

export const TPR_THROUGHPUT_DETAILS_BY_PRODUCT_FORM = new InjectionToken<ProductsArrayForm>(
  'TPR throughput details form',
);

export const tprThroughputDetailsByProductFormProvider: Provider = {
  provide: TPR_THROUGHPUT_DETAILS_BY_PRODUCT_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const performanceData = requestTaskStore.select(tprFormQuery.selectPerformanceData)();
    const referenceData = requestTaskStore.select(tprFormQuery.selectReferenceData)();

    const savedProducts = performanceData?.throughputDetails?.variableEnergyConsumptionDataByProduct ?? [];
    const savedProductsByName = new Map(savedProducts.map((product) => [product.productName, product]));
    const products = referenceData?.baselineAndTargets?.variableEnergyConsumptionDataByProduct ?? [];

    const zeroEnergyValidator = createZeroEnergyValidatorFn(
      performanceData?.energyFuelDetails,
      referenceData?.baselineAndTargets,
    );

    return fb.group(
      {
        products: fb.array(products.map((p) => createProductRowForm(fb, p, savedProductsByName.get(p.productName)))),
      },
      { validators: [zeroEnergyValidator] },
    );
  },
};

function createProductRowForm(
  fb: FormBuilder,
  product: ProductVariableEnergyConsumptionData,
  savedProduct?: PerformanceDataFacilityProductVariableEnergyData,
): ProductRowForm {
  return fb.group({
    productName: fb.control<string>(product.productName),
    baselineYear: fb.control<number>(product.baselineYear),
    energy: fb.control<string>(product.energy),
    targetImprovement: fb.control<number>(0),
    actualThroughput: fb.control<string>(savedProduct?.actualThroughput ?? '', {
      validators: [
        GovukValidators.required('Enter the actual throughput'),
        GovukValidators.min(0, 'Enter a value equal to or greater than 0'),
        CCAGovukValidators.maxDecimalsWithMessage(7, 'Enter a number up to 7 decimal places'),
      ],
      updateOn: 'change',
    }),
    throughputUnit: fb.control<string>(product.throughputUnit),
    adjustedThroughput: fb.control<number>(0),
    targetEnergy: fb.control<number>(0),
  });
}
