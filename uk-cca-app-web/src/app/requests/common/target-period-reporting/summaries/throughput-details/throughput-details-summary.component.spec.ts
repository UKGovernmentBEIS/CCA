import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { PaginationComponent } from '@shared/components';

import { PerformanceDataFacilityInputData, PerformanceDataFacilityReferenceData } from 'cca-api';

import { roundHalfUpTo7Decimals } from '../../utils';
import { ThroughputDetailsSummaryComponent } from './throughput-details-summary.component';

describe('ThroughputDetailsSummaryComponent', () => {
  let component: ThroughputDetailsSummaryComponent;
  let fixture: ComponentFixture<ThroughputDetailsSummaryComponent>;

  const buildBaselineData = (productCount = 2) => ({
    baselineAndTargets: {
      measurementType: 'ENERGY_KWH',
      usedReportingMechanism: false,
      baselineDate: '2022-01-01',
      improvements: { TP5: '0', TP6: '0', TP7: '0', TP8: '0', TP9: '0' },
      variableEnergyConsumptionDataByProduct: Array.from({ length: productCount }, (_, i) => ({
        productName: `Product ${i + 1}`,
        baselineYear: 2022,
        productStatus: 'LIVE',
        energy: '1',
        throughputUnit: 'tonnes',
        throughput: '1',
      })),
    },
  });

  const buildPerformanceData = (enteredIndexes: number[]): PerformanceDataFacilityInputData => ({
    energyFuelDetails: {
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
        NON_GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      electricitySuppliedFromCHP: '0',
      atLeastSeventyPercentEnergyUsed: true,
    },
    throughputDetails: {
      totalTargetVariableEnergy: '0',
      variableEnergyConsumptionDataByProduct: enteredIndexes.map((index) => ({
        productName: `Product ${index + 1}`,
        actualThroughput: '10',
        targetImprovement: '0',
        adjustedThroughput: '0',
        targetEnergy: '0',
      })),
    },
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThroughputDetailsSummaryComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(ThroughputDetailsSummaryComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('referenceData', buildBaselineData(1) as PerformanceDataFacilityReferenceData);
    fixture.componentRef.setInput('performanceData', buildPerformanceData([0]));
    fixture.componentRef.setInput('reportType', 'FINAL');
    fixture.componentRef.setInput('targetPeriodType', 'TP5');

    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render only products with entered throughput data', () => {
    fixture.componentRef.setInput('referenceData', buildBaselineData(3) as PerformanceDataFacilityReferenceData);
    fixture.componentRef.setInput('performanceData', buildPerformanceData([0, 2]));
    fixture.detectChanges();

    const content = fixture.nativeElement.textContent;

    expect(content).toContain('Product 1');
    expect(content).toContain('Product 3');
    expect(content).not.toContain('Product 2');
  });

  it('should calculate and render total target variable energy for entered products', () => {
    fixture.componentRef.setInput('referenceData', buildBaselineData(3) as PerformanceDataFacilityReferenceData);
    fixture.componentRef.setInput('performanceData', buildPerformanceData([0, 2]));
    fixture.detectChanges();

    // 2 entered products x (energy 1 * throughput 10 * (1 - 0%))
    expect(fixture.nativeElement.textContent).toContain('20');
  });

  it('should display pagination only when there are more than 10 entered products', () => {
    fixture.componentRef.setInput('referenceData', buildBaselineData(11) as PerformanceDataFacilityReferenceData);
    fixture.componentRef.setInput('performanceData', buildPerformanceData(Array.from({ length: 11 }, (_, i) => i)));
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.directive(PaginationComponent))).toBeTruthy();
  });

  it('should show interim target header for interim reports', () => {
    fixture.componentRef.setInput('reportType', 'INTERIM');
    fixture.detectChanges();

    const content = fixture.nativeElement.textContent;

    expect(content).toContain('Interim target %');
    expect(content).not.toContain('Improvement target %');
  });

  it('should format actual throughput with thousand separators', () => {
    const performanceData = buildPerformanceData([0]);
    performanceData.throughputDetails!.variableEnergyConsumptionDataByProduct![0].actualThroughput = '12345.6789012';
    fixture.componentRef.setInput('performanceData', performanceData);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('12,345.6789012');
  });

  it('should display CO2e column headers and labels for carbon-based facilities', () => {
    const carbonBaseline = buildBaselineData(2);
    carbonBaseline.baselineAndTargets.measurementType = 'CARBON_TONNE';
    fixture.componentRef.setInput('referenceData', carbonBaseline as PerformanceDataFacilityReferenceData);
    fixture.componentRef.setInput('performanceData', buildPerformanceData([0, 1]));
    fixture.detectChanges();

    const content = fixture.nativeElement.textContent;

    // Column headers
    expect(content).toContain('Baseline CO2e intensity');
    expect(content).not.toContain('Baseline energy intensity');
    expect(content).toContain('Target CO2e');
    expect(content).not.toContain('Target energy');

    // Section labels
    expect(content).toContain('Calculated carbon dioxide amounts');
    expect(content).not.toContain('Calculated energy amounts');
    expect(content).toContain('Total target variable carbon dioxide');
    expect(content).not.toContain('Total target variable energy');
  });

  it('should produce display values matching roundHalfUpTo7Decimals for computed values', () => {
    const baseline = {
      baselineAndTargets: {
        measurementType: 'ENERGY_KWH',
        usedReportingMechanism: false,
        baselineDate: '2022-01-01',
        improvements: { TP5: '8', TP6: '0', TP7: '0', TP8: '0', TP9: '0' },
        variableEnergyConsumptionDataByProduct: [
          {
            productName: 'Widgets',
            baselineYear: 2022,
            productStatus: 'LIVE',
            energy: '605377',
            throughputUnit: 'tonnes',
            throughput: '1000',
          },
        ],
      },
    };

    const perf: PerformanceDataFacilityInputData = {
      energyFuelDetails: {
        standardFuels: {
          GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
          NON_GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
        },
        electricitySuppliedFromCHP: '0',
        atLeastSeventyPercentEnergyUsed: true,
      },
      throughputDetails: {
        totalTargetVariableEnergy: '0',
        variableEnergyConsumptionDataByProduct: [
          {
            productName: 'Widgets',
            actualThroughput: '999.99999',
            targetImprovement: '0',
            adjustedThroughput: '0',
            targetEnergy: '0',
          },
        ],
      },
    };

    fixture.componentRef.setInput('referenceData', baseline as PerformanceDataFacilityReferenceData);
    fixture.componentRef.setInput('performanceData', perf);
    fixture.detectChanges();

    const total = component.totalTargetVariableEnergy();
    const displayed = component.displayRounded(total);
    const apiString = roundHalfUpTo7Decimals(total);

    expect(displayed.toFixed(7)).toBe(Number(apiString).toFixed(7));
  });
});
