import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockSectorDetails } from '../specs/fixtures/mock';
import { ActiveSectorStore } from './active-sector.store';
import { SectorComponent } from './sector.component';

describe('SectorComponent', () => {
  let store: ActiveSectorStore;
  beforeEach(async () => {
    await render(SectorComponent, {
      providers: [ActiveSectorStore],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ sectorId: 1 }) });
        store = testbed.inject(ActiveSectorStore);
        store.setState(mockSectorDetails);
      },
    });
  });

  it('should render title', () => {
    const title = `${mockSectorDetails.sectorAssociationDetails.acronym} - ${mockSectorDetails.sectorAssociationDetails.commonName}`;

    expect(screen.getByText(title)).toBeInTheDocument();
  });

  it('should contain tabs "Details", "Scheme", "Contacts" and "Target units"', () => {
    const tabTitles = ['Details', 'Scheme', 'Contacts', 'Target units'];

    expect(screen.getAllByRole('tab')).toHaveLength(4);
    expect(screen.getAllByRole('tab')[0]).toHaveTextContent(tabTitles[0]);
    expect(screen.getAllByRole('tab')[1]).toHaveTextContent(tabTitles[1]);
    expect(screen.getAllByRole('tab')[2]).toHaveTextContent(tabTitles[2]);
    expect(screen.getAllByRole('tab')[3]).toHaveTextContent(tabTitles[3]);
  });
});
