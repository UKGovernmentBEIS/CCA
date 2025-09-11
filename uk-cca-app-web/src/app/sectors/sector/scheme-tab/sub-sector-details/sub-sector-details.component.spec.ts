import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockSubSectorDetails } from '../../../specs/fixtures/mock';
import { toSubsectorSchemeSummaryData } from '../scheme-summary-data';
import { SubSectorDetailsComponent } from './sub-sector-details.component';

describe('SubSectorDetailsComponent', () => {
  beforeEach(async () => {
    await render(SubSectorDetailsComponent, {
      componentProviders: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, { subSector: mockSubSectorDetails }, ''),
        },
      ],
    });
  });

  it('should render the subsector name in the page heading', () => {
    const pageHeading = document.querySelector('netz-page-heading');
    expect(pageHeading).toHaveTextContent('sub-sector-name');
  });

  it('should render all section titles', () => {
    expect(screen.getByText('CCA2 (2013-2024)')).toBeInTheDocument();
    expect(screen.getByText('Details')).toBeInTheDocument();
    expect(screen.getByText('Sector commitment')).toBeInTheDocument();
  });

  it('should render target details', () => {
    expect(screen.getByText('Target type')).toBeInTheDocument();
    expect(screen.getByText('Relative')).toBeInTheDocument();
    expect(screen.getByText('Throughput unit')).toBeInTheDocument();
    expect(screen.getByText('tonne')).toBeInTheDocument();
    expect(screen.getByText('Energy or Carbon unit')).toBeInTheDocument();
    expect(screen.getByText('kWh')).toBeInTheDocument();
  });

  it('should render sector commitments', () => {
    // Check that target periods are rendered
    expect(screen.getByText('2010-2011')).toBeInTheDocument();
    expect(screen.getByText('2020-2021')).toBeInTheDocument();

    // Check that percentages are formatted correctly (0.10 becomes 10%, 0.15 becomes 15%)
    expect(screen.getByText('10%')).toBeInTheDocument();
    expect(screen.getByText('15%')).toBeInTheDocument();
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
