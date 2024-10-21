import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockSectorDetails } from '../../specs/fixtures/mock';
import { ActiveSectorStore } from '../active-sector.store';
import { SectorDetailsTabComponent } from './sector-details-tab.component';

describe('SectorDetailsTabComponent', () => {
  let store: ActiveSectorStore;
  beforeEach(async () => {
    await render(SectorDetailsTabComponent, {
      providers: [ActiveSectorStore],
      configureTestBed: (testbed) => {
        store = testbed.inject(ActiveSectorStore);
        store.setState(mockSectorDetails);
      },
    });
  });

  it('should render "Sector details" and "Sector contact" titles', () => {
    expect(screen.getByText('Sector details')).toBeTruthy();
    expect(screen.getByText('Sector contact')).toBeTruthy();
  });

  it('should render "details" section', () => {
    const detailsList = document.querySelectorAll("[data-testid='details-list'] div");

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Sector name', 'common'],
      ['Sector acronym', 'acronym'],
      ['Sector / Trade association name', 'legal'],
      ['Sector facilitator', 'facilitator name facilitator last name'],
      ['Address for service of notices', ' address 1 city 1  12345 '],
      ['Energy intensive or EPR', 'EPR'],
    ]);
  });

  it('should render "contacts" section', () => {
    const contactList = document.querySelectorAll("[data-testid='contact-list'] div");

    const elements = [];

    contactList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Title', 'Mr.'],
      ['First name', 'John'],
      ['Last name', 'Doe'],
      ['Job title', 'job title'],
      ['Organisation name', 'org name'],
      ['Address', ' address 1 city 1  12345 '],
      ['Phone number', '123456789'],
      ['Email address', 'johny@doe.com'],
    ]);
  });

  it('should render 11 change links', () => {
    expect(screen.getAllByText(/Change/i)).toHaveLength(11);
  });
});
