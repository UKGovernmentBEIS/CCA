import { HttpClientTestingModule } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { RegulatorAuthoritiesService } from 'cca-api';

import { mockRegulatorsRouteData } from '../testing/mock-data';
import { RegulatorsUsersComponent } from './regulators-users.component';

describe('RegulatorUsersComponent', () => {
  const regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: jest.fn().mockReturnValue(of(mockRegulatorsRouteData.regulators)),
    updateCompetentAuthorityRegulatorUsersStatus: jest.fn().mockReturnValue(of(null)),
  };

  const updateSpy = jest.spyOn(regulatorAuthoritiesService, 'updateCompetentAuthorityRegulatorUsersStatus');

  beforeEach(async () => {
    const component = await render(RegulatorsUsersComponent, {
      configureTestBed: (testbed) => {
        const authStore = testbed.inject(AuthStore);
        authStore.setUserState({
          status: 'ENABLED',
          roleType: 'REGULATOR',
          userId: '5reg',
        });
      },
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: RegulatorAuthoritiesService,
          useValue: regulatorAuthoritiesService,
        },
      ],
    });

    component.fixture.detectChanges();
  });

  it('should render', () => {
    expect(document.getElementById('regulators-form')).toBeInTheDocument();
  });

  it('should have as many rows as regulators', async () => {
    const rows = screen.getAllByRole('row');
    expect(rows).toHaveLength(mockRegulatorsRouteData.regulators.caUsers.length + 1);
  });

  it('should edit a regulator and save', async () => {
    const user = UserEvent.setup();
    const select = document.getElementById('regulatorsArray.1.authorityStatus');
    await user.selectOptions(select, '1: DISABLED');
    expect(select).toHaveValue('1: DISABLED');
    await user.click(screen.getByText('Save'));
    expect(updateSpy).toHaveBeenCalledTimes(1);
  });
});
