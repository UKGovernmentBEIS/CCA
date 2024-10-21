import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockSubSectorDetails } from '../../../specs/fixtures/mock';
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

  it('should render all section titles', () => {
    expect(screen.getByText('Subsector details')).toBeInTheDocument();
    expect(screen.getByText('Target currency')).toBeInTheDocument();
    expect(screen.getByText('Sector commitment')).toBeInTheDocument();
  });

  it('should render "Subsector details" section', () => {
    const umbrellaList = document.querySelectorAll("[data-testid='details-list'] div");

    const elements = [];

    umbrellaList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([['Name', 'sub-sector-name']]);
  });

  it('should render "Target currency" section', () => {
    const umbrellaList = document.querySelectorAll("[data-testid='target-list'] div");

    const elements = [];

    umbrellaList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Target type', 'Relative'],
      ['Throughput unit', 'tonne'],
      ['Energy or Carbon unit', 'kWh'],
    ]);
  });

  it('should populate the table accordingly', async () => {
    const table = document.querySelector('govuk-table');

    mockSubSectorDetails.targetSet.targetCommitments.forEach(async (el) => {
      const targetImprovementEl = await screen.findByText(el.targetImprovement);
      const targetPeriodEl = await screen.findByText(el.targetPeriod);

      expect(table).toContainElement(targetImprovementEl);
      expect(table).toContainElement(targetPeriodEl);
    });
  });
});
