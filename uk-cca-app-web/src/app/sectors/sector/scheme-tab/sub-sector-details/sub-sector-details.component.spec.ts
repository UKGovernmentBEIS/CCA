import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { mockSubSectorDetails } from '../../../specs/fixtures/mock';
import { toSubsectorSchemeSummaryData } from '../scheme-summary-data';
import { SubSectorDetailsComponent } from './sub-sector-details.component';

describe('SubSectorDetailsComponent', () => {
  let fixture: ComponentFixture<SubSectorDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubSectorDetailsComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, { subSector: mockSubSectorDetails }, ''),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SubSectorDetailsComponent);
    fixture.detectChanges();
  });

  it('should render the subsector name in the page heading', () => {
    const pageHeading = document.querySelector('netz-page-heading');
    expect((pageHeading as HTMLElement | null)?.textContent ?? '').toContain('sub-sector-name');
  });

  it('should render all section titles', () => {
    expect(getByText('CCA2 (2013-2024)')).toBeTruthy();
    expect(getByText('Details')).toBeTruthy();
    expect(getByText('Sector commitment')).toBeTruthy();
  });

  it('should render target details', () => {
    expect(getByText('Target type')).toBeTruthy();
    expect(getByText('Relative')).toBeTruthy();
    expect(getByText('Throughput unit')).toBeTruthy();
    expect(getByText('tonne')).toBeTruthy();
    expect(getByText('Energy or Carbon unit')).toBeTruthy();
    expect(getByText('kWh')).toBeTruthy();
  });

  it('should render sector commitments', () => {
    // Check that target periods are rendered
    expect(getByText('2010-2011')).toBeTruthy();
    expect(getByText('2020-2021')).toBeTruthy();

    // Check that percentages are formatted correctly (0.10 becomes 10%, 0.15 becomes 15%)
    expect(getByText('10%')).toBeTruthy();
    expect(getByText('15%')).toBeTruthy();
  });

  describe('toSubsectorSchemeSummaryData', () => {
    it('should sort target periods correctly when given 10 unsorted target periods', () => {
      const mockSubsectorScheme: any = {
        name: 'Test Subsector',
        subsectorAssociationSchemeMap: {
          CCA_2: {
            targetSet: {
              targetCurrencyType: 'Relative',
              throughputUnit: 'tonne',
              energyOrCarbonUnit: 'kWh',
              targetCommitments: [
                { targetPeriod: 'TP10', targetImprovement: '0.10' },
                { targetPeriod: 'TP01', targetImprovement: '0.01' },
                { targetPeriod: 'TP05', targetImprovement: '0.05' },
                { targetPeriod: 'TP02', targetImprovement: '0.02' },
                { targetPeriod: 'TP08', targetImprovement: '0.08' },
                { targetPeriod: 'TP03', targetImprovement: '0.03' },
                { targetPeriod: 'TP09', targetImprovement: '0.09' },
                { targetPeriod: 'TP04', targetImprovement: '0.04' },
                { targetPeriod: 'TP06', targetImprovement: '0.06' },
                { targetPeriod: 'TP07', targetImprovement: '0.07' },
              ],
            },
          },
        },
      };

      const summaryData = toSubsectorSchemeSummaryData(mockSubsectorScheme);

      // Find the section with Sector commitment
      const sectorCommitmentSection = summaryData.find((section) => section.header === 'Sector commitment');

      // Extract the target period data - the rows are directly in the section
      const targetPeriodRows = sectorCommitmentSection.data;

      // Check that target periods are sorted correctly
      const expectedOrder = ['TP01', 'TP02', 'TP03', 'TP04', 'TP05', 'TP06', 'TP07', 'TP08', 'TP09', 'TP10'];
      const actualOrder = targetPeriodRows.map((row) => row.key);

      expect(actualOrder).toEqual(expectedOrder);
    });
  });
});
