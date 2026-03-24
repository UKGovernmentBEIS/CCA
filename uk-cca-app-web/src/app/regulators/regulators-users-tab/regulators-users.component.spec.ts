import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { click, getAllByRole, getByText } from '@testing';

import { RegulatorAuthoritiesService } from 'cca-api';

import { mockRegulatorsRouteData } from '../testing/mock-data';
import { RegulatorsUsersComponent } from './regulators-users.component';

describe('RegulatorUsersComponent', () => {
  let fixture: ComponentFixture<RegulatorsUsersComponent>;
  let updateSpy: jest.SpyInstance;

  const regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: jest.fn().mockReturnValue(of(mockRegulatorsRouteData.regulators)),
    updateCompetentAuthorityRegulatorUsersStatus: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    updateSpy = jest.spyOn(regulatorAuthoritiesService, 'updateCompetentAuthorityRegulatorUsersStatus');

    await TestBed.configureTestingModule({
      imports: [RegulatorsUsersComponent],
      providers: [
        provideRouter([]),
        provideHttpClientTesting(),
        {
          provide: RegulatorAuthoritiesService,
          useValue: regulatorAuthoritiesService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegulatorsUsersComponent);

    const authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      status: 'ENABLED',
      roleType: 'REGULATOR',
      userId: '5reg',
    });

    fixture.detectChanges();
  });

  it('should render', () => {
    expect(document.getElementById('regulators-form')).toBeTruthy();
  });

  it('should have as many rows as regulators', async () => {
    const rows = getAllByRole('row');
    expect(rows).toHaveLength(mockRegulatorsRouteData.regulators.caUsers.length + 1);
  });

  it('should edit a regulator and save', async () => {
    const select = document.getElementById('regulatorsArray.1.authorityStatus') as HTMLSelectElement;
    select.value = '1: DISABLED';
    select.dispatchEvent(new Event('change', { bubbles: true }));
    expect((select as HTMLInputElement | HTMLSelectElement | null)?.value ?? '').toBe('1: DISABLED');
    click(getByText('Save'));
    expect(updateSpy).toHaveBeenCalledTimes(1);
  });
});
