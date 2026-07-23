import { canRemoveVariableEnergyProduct, mapToProductVariableEnergyConsumptionData } from './transform';

describe('canRemoveVariableEnergyProduct', () => {
  it('allows a new product to be removed when another product remains', () => {
    expect(canRemoveVariableEnergyProduct('NEW', 2)).toBe(true);
  });

  it.each(['LIVE', 'EXCLUDED'] as const)('does not allow a %s product to be removed', (productStatus) => {
    expect(canRemoveVariableEnergyProduct(productStatus, 2)).toBe(false);
  });

  it('does not allow the only product to be removed', () => {
    expect(canRemoveVariableEnergyProduct('NEW', 1)).toBe(false);
  });
});

describe('mapToProductVariableEnergyConsumptionData', () => {
  it.each(['LIVE', 'EXCLUDED'] as const)('retains %s status when a product is renamed', (productStatus) => {
    const result = mapToProductVariableEnergyConsumptionData({
      productName: 'Renamed product',
      productStatus,
      baselineYear: 2022,
      baselineVariableEnergy: '100',
      baselineThroughput: '50',
      throughputUnit: 'tonnes',
    });

    expect(result.productName).toBe('Renamed product');
    expect(result.productStatus).toBe(productStatus);
  });
});
