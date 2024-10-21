import { calculateAbsoluteTarget, calculatePerformance, calculateRelativeTarget } from './baseline-and-targets.helper';

describe('Baseline and Targets calculations', () => {
  it('should correctly calculate performance', () => {
    const performanceTestCases = [
      { energy: 100, throughput: 50, expectedPerformance: 2 },
      { energy: 500, throughput: 250, expectedPerformance: 2 },
      { energy: 1234, throughput: 5678, expectedPerformance: 0.21733 },
      { energy: 0, throughput: 100, expectedPerformance: 0 },
      { energy: 100, throughput: 0, expectedPerformance: Infinity },
      { energy: Infinity, throughput: 100, expectedPerformance: Infinity },
      { energy: 100, throughput: Infinity, expectedPerformance: 0 },
      { energy: NaN, throughput: 100, expectedPerformance: null },
      { energy: 100, throughput: NaN, expectedPerformance: null },
      { energy: 123456789012345, throughput: 98765432109876, expectedPerformance: 1.25 },
      { energy: 1e15, throughput: 1e12, expectedPerformance: 1000 },
      { energy: 0.0001, throughput: 0.0002, expectedPerformance: 0.5 },
      { energy: 1e-10, throughput: 1e-12, expectedPerformance: 100 },
    ];

    performanceTestCases.forEach(({ energy, throughput, expectedPerformance }) => {
      expect(calculatePerformance(energy, throughput)).toEqual(expectedPerformance);
    });
  });

  it('should correctly calculate baseline for ABSOLUTE', () => {
    const testCases = [
      { energyOrCarbon: 100, improvement: 20, extendedPeriod: false, expectedTarget: 80 },
      { energyOrCarbon: 500, improvement: 40, extendedPeriod: false, expectedTarget: 300 },
      { energyOrCarbon: 1234, improvement: 15, extendedPeriod: false, expectedTarget: 1048.9 },
      { energyOrCarbon: 0, improvement: 50, extendedPeriod: false, expectedTarget: 0 },
      { energyOrCarbon: 100, improvement: 10, extendedPeriod: false, expectedTarget: 90 },
      { energyOrCarbon: 100, improvement: -10, extendedPeriod: false, expectedTarget: 110 },
      { energyOrCarbon: Infinity, improvement: 20, extendedPeriod: false, expectedTarget: Infinity },
      { energyOrCarbon: 100, improvement: Infinity, extendedPeriod: false, expectedTarget: -Infinity },
      { energyOrCarbon: NaN, improvement: 20, extendedPeriod: false, expectedTarget: null },
      { energyOrCarbon: 100, improvement: NaN, extendedPeriod: false, expectedTarget: null },
      { energyOrCarbon: 100, improvement: 20, extendedPeriod: true, expectedTarget: 160 },
      { energyOrCarbon: 500, improvement: 40, extendedPeriod: true, expectedTarget: 600 },
      { energyOrCarbon: 1234, improvement: 15, extendedPeriod: true, expectedTarget: 2097.8 },
      { energyOrCarbon: 123456789012345, improvement: 30, extendedPeriod: false, expectedTarget: 86419752308641.5 },
      { energyOrCarbon: 1e15, improvement: 20, extendedPeriod: false, expectedTarget: 8e14 },
      { energyOrCarbon: 0.0001, improvement: 20, extendedPeriod: false, expectedTarget: 0.00008 },
      { energyOrCarbon: 1e-10, improvement: 30, extendedPeriod: false, expectedTarget: 0 },
    ];

    testCases.forEach(({ energyOrCarbon, improvement, extendedPeriod, expectedTarget }) => {
      expect(calculateAbsoluteTarget(energyOrCarbon, improvement, extendedPeriod)).toEqual(expectedTarget);
    });
  });

  it('should correctly calculate baseline for RELATIVE', () => {
    const testCases = [
      { performance: 100, improvement: 20, expectedTarget: 80 },
      { performance: 50, improvement: 30, expectedTarget: 35 },
      { performance: 1234, improvement: 15, expectedTarget: 1048.9 },
      { performance: 0, improvement: 50, expectedTarget: 0 },
      { performance: 100, improvement: 100, expectedTarget: 0 },
      { performance: 100, improvement: -10, expectedTarget: 110 },
      { performance: Infinity, improvement: 20, expectedTarget: Infinity },
      { performance: 100, improvement: Infinity, expectedTarget: -Infinity },
      { performance: NaN, improvement: 20, expectedTarget: null },
      { performance: 100, improvement: NaN, expectedTarget: null },
      { performance: 123456789012345, improvement: 30, expectedTarget: 86419752308641.5 },
      { performance: 1e15, improvement: 20, expectedTarget: 8e14 },
      { performance: 0.0001, improvement: 20, expectedTarget: 0.00008 },
      { performance: 1e-10, improvement: 30, expectedTarget: 0 },
    ];

    testCases.forEach(({ performance, improvement, expectedTarget }) => {
      expect(calculateRelativeTarget(performance, improvement)).toEqual(expectedTarget);
    });
  });
});
