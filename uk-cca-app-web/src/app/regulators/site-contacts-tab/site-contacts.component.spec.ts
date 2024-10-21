import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { transformUsername } from '@netz/common/pipes';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { RegulatorAuthoritiesService, SectorAssociationsSiteContactsService } from 'cca-api';

import { mockSiteContactsRouteData } from '../testing/mock-site-contacts-data';
import { SiteContactsComponent } from './site-contacts.component';

const queryParamMap = new Map([['page', 1]]);

describe('SiteContactsComponent', () => {
  let sectorAssociationsSiteContactsServiceMock: Partial<jest.Mocked<SectorAssociationsSiteContactsService>>;
  let regulatorAuthoritiesServiceMock: Partial<jest.Mocked<RegulatorAuthoritiesService>>;
  let updateSpy: jest.SpyInstance;
  let getSpy: jest.SpyInstance;
  beforeEach(async () => {
    sectorAssociationsSiteContactsServiceMock = {
      getSectorAssociationSiteContacts: jest
        .fn()
        .mockImplementation(() => of(mockSiteContactsRouteData().siteContactsInfo)),
      updateSectorAssociationSiteContacts: jest.fn().mockReturnValue(of(null)),
    };
    regulatorAuthoritiesServiceMock = {
      getCaRegulators: jest.fn().mockImplementation(() => of(mockSiteContactsRouteData().regulators)),
    };

    updateSpy = jest.spyOn(sectorAssociationsSiteContactsServiceMock, 'updateSectorAssociationSiteContacts');
    getSpy = jest.spyOn(sectorAssociationsSiteContactsServiceMock, 'getSectorAssociationSiteContacts');
    const component = await render(SiteContactsComponent, {
      imports: [HttpClientTestingModule],
      componentProviders: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of(mockSiteContactsRouteData()),
            queryParamMap: of(queryParamMap),
            snapshot: { queryParamMap },
          },
        },
        {
          provide: SectorAssociationsSiteContactsService,
          useValue: sectorAssociationsSiteContactsServiceMock,
        },
        {
          provide: RegulatorAuthoritiesService,
          useValue: regulatorAuthoritiesServiceMock,
        },
      ],
    });
    component.fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should render table headers', () => {
    expect(screen.getByTestId('site-contacts-table')).toContainElement(screen.getByText('Sector Name'));
    expect(screen.getByTestId('site-contacts-table')).toContainElement(screen.getByText('Assigned to'));
  });

  it('should correctly populate the first select field options', () => {
    const regulatorNames = mockSiteContactsRouteData()
      .regulators.caUsers.filter((u) => u.authorityStatus === 'ACTIVE')
      .map((r) => transformUsername(r));
    const select = document.getElementById('siteContacts.0.userId');
    regulatorNames.forEach((n) => expect(select).toContain(screen.getAllByText(n)[0]));
  });
  it('should include only active regulators as select options', () => {
    const select = document.getElementById('siteContacts.0.userId') as HTMLSelectElement | null;
    expect(select).not.toBeNull();
    expect(select.options).toHaveLength(2);
  });
  it('should populate the table', () => {
    // arrange
    const table = screen.getByTestId('site-contacts-table');
    const rows = table.querySelectorAll('tbody .govuk-table__header');
    const regulatorsMap = new Map();
    mockSiteContactsRouteData().regulators.caUsers.forEach((r) => regulatorsMap.set(r.userId, transformUsername(r)));

    // assert
    expect(rows).toHaveLength(mockSiteContactsRouteData().siteContactsInfo.siteContacts.length);

    mockSiteContactsRouteData().siteContactsInfo.siteContacts.forEach((v, idx) => {
      const select = document.getElementById(`siteContacts.${idx}.userId`);
      const options = select.querySelectorAll('option');
      const selectedOption = Array.from(options).find((o) => o.selected);
      expect(rows[idx]).toHaveTextContent(v.sectorName);
      expect(selectedOption.textContent.trim()).toEqual(regulatorsMap.get(v.userId) || 'Unassigned');
    });
  });

  it('should discard changes properly', async () => {
    const user = UserEvent.setup();
    const select = screen.getAllByRole('combobox')[0] as HTMLSelectElement;
    expect(select).toHaveDisplayValue(/Regulator Admin/i);
    await user.selectOptions(select, '0: null');
    expect(select).toHaveDisplayValue(/Unassigned/i);
    await user.click(screen.getByText(/Discard changes/i));
    expect(getSpy).toHaveBeenCalled();
    expect(screen.getAllByRole('combobox')[0]).toHaveDisplayValue(/Regulator Admin/i);
  });

  it('should update form post submit', async () => {
    const user = UserEvent.setup();
    const select = document.getElementById('siteContacts.0.userId') as HTMLSelectElement;
    await user.selectOptions(select, '0: null');
    expect(select).toHaveDisplayValue(/Unassigned/i);
    await user.click(screen.getByText('Save'));
    expect(updateSpy).toHaveBeenCalledTimes(1);
    expect(updateSpy).toHaveBeenCalledWith([
      { sectorAssociationId: 1, userId: null },
      { sectorAssociationId: 2, userId: '489aa8e5-c0af-45f6-b418-10749341bf62' },
      { sectorAssociationId: 3, userId: null },
      { sectorAssociationId: 4, userId: null },
    ]);
  });
});
