import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { FacilityInfoViewService, FacilitySearchResults } from 'cca-api';

import { FacilitiesListComponent } from './facilities-list.component';

describe('FacilitiesListComponent', () => {
  let component: FacilitiesListComponent;
  let fixture: ComponentFixture<FacilitiesListComponent>;
  let facilityInfoViewService: Partial<jest.Mocked<FacilityInfoViewService>>;

  const mockFacilities: FacilitySearchResults = {
    facilities: [
      {
        id: 'ADS_1-F00001',
        siteName: 'fac1-1',
        schemeExitDate: new Date().toISOString(),
        status: 'LIVE',
      },
      {
        id: 'ADS_1-F00002',
        siteName: 'fac1-2',
        schemeExitDate: new Date().toISOString(),
        status: 'INACTIVE',
      },
    ],
    total: 2,
  };

  beforeEach(async () => {
    facilityInfoViewService = {
      searchFacilities: jest.fn().mockReturnValue(of(mockFacilities)),
    };

    await TestBed.configureTestingModule({
      imports: [FacilitiesListComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: FacilityInfoViewService, useValue: facilityInfoViewService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FacilitiesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the facilities list table and have 3 rows (including header)', () => {
    expect(screen.getByTestId('facilities-list-table')).toBeVisible();
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(3);
  });

  it('should show two facility entries', () => {
    expect(screen.getByText('ADS_1-F00001')).toBeInTheDocument();
    expect(screen.getByText('ADS_1-F00002')).toBeInTheDocument();
  });

  it('should show one facility entry after search', async () => {
    const user = UserEvent.setup();
    const searchInput = screen.getByLabelText('Find a facility');
    const searchBtn = screen.getByTestId('search-btn');

    facilityInfoViewService.searchFacilities = jest.fn().mockReturnValue(
      of({
        facilities: [mockFacilities.facilities[0]],
        total: 1,
      }),
    );

    await user.click(searchInput);
    await user.type(searchInput, 'F00001');
    await user.click(searchBtn);

    fixture.detectChanges();

    expect(screen.getByText('ADS_1-F00001')).toBeInTheDocument();
    expect(screen.queryByText('ADS_1-F00002')).not.toBeInTheDocument();
  });

  it('should not show any entry after invalid search', async () => {
    const user = UserEvent.setup();
    const searchInput = screen.getByLabelText('Find a facility');
    const searchBtn = screen.getByTestId('search-btn');

    facilityInfoViewService.searchFacilities = jest.fn().mockReturnValue(of({}));

    await user.click(searchInput);
    await user.type(searchInput, 'mplah');
    await user.click(searchBtn);

    fixture.detectChanges();

    expect(screen.queryByText('ADS_1-F00001')).not.toBeInTheDocument();
    expect(screen.queryByText('ADS_1-F00002')).not.toBeInTheDocument();
  });
});
