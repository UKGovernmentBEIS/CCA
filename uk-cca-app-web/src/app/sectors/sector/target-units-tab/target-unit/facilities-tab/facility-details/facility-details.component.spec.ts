import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText, getSummaryListData } from '@testing';

import { FacilityInfoDTO } from 'cca-api';

import { mockAuthState } from '../../../../../specs/fixtures/mock';
import { mockFacilityDetails } from '../testing/mock-data';
import { FacilityDetailsComponent } from './facility-details.component';

describe('FacilityDetailsComponent', () => {
  let component: FacilityDetailsComponent;
  let fixture: ComponentFixture<FacilityDetailsComponent>;
  let facilityDetails: FacilityInfoDTO;
  let route: ActivatedRouteStub;

  beforeEach(async () => {
    facilityDetails = {
      ...mockFacilityDetails,
      surplusDetails: { schemeVersion: 'CCA_3', surplusGained: '1627', hasHistory: true },
    };
    route = new ActivatedRouteStub(null, null, { facilityDetails });
    await TestBed.configureTestingModule({
      imports: [FacilityDetailsComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: route,
        },
      ],
    }).compileComponents();

    TestBed.inject(AuthStore).setState(mockAuthState);
    fixture = TestBed.createComponent(FacilityDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render all section titles', () => {
    expect(getByText('Facility details')).toBeTruthy();
    expect(getByText('Subsistence fees')).toBeTruthy();
  });

  it('should render "Facility details" section', () => {
    expect(getSummaryListData(fixture.nativeElement)[0]).toEqual([
      ['Site name', 'Address'],
      ['Fac 1', 'address line 1city505050country'],
    ]);
  });

  it('should render "Subsistence fees" section', () => {
    expect(getSummaryListData(fixture.nativeElement)[1]).toEqual([
      ['Subsistence fees start date', 'Scheme exit date'],
      ['01/01/2024', '02/02/2024'],
    ]);
  });

  it('should display CCA3 surplus with Change and View history for a regulator', () => {
    const surplus = fixture.nativeElement.querySelector('[data-testid="surplus"]');
    expect(surplus.textContent).toContain('Surplus gained (tCO2e)');
    expect(surplus.textContent).toContain('1,627');
    expect(surplus.querySelector('a[href="/edit-surplus"]')).toBeTruthy();
    expect(surplus.querySelector('a[href="/surplus-history"]')).toBeTruthy();
    expect(surplus.querySelectorAll('a')).toHaveLength(2);
  });

  it('should update facility details and surplus actions when resolved data changes', () => {
    route.setResolveMap({
      facilityDetails: {
        ...facilityDetails,
        facilityBusinessId: 'AIC-F00041',
        siteName: 'Updated site',
        status: 'INACTIVE',
        surplusDetails: { schemeVersion: 'CCA_2', surplusGained: '42', hasHistory: true },
      } satisfies FacilityInfoDTO,
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('AIC-F00041');
    expect(fixture.nativeElement.querySelector('h1').textContent).toContain('Updated site');
    expect(fixture.nativeElement.querySelector('govuk-tag .govuk-tag--red')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('[data-testid="facility-details"]').textContent).toContain(
      'Updated site',
    );
    const surplus = fixture.nativeElement.querySelector('[data-testid="surplus"]');
    expect(surplus.querySelector('dd').textContent.trim()).toBe('42');
    expect(surplus.querySelector('a')).toBeNull();

    route.setResolveMap({ facilityDetails: { ...facilityDetails, surplusDetails: undefined } });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="surplus"]')).toBeNull();
  });

  it.each(['SECTOR_USER', 'OPERATOR'] as const)('should hide Change for %s', (roleType) => {
    TestBed.inject(AuthStore).setState({
      ...mockAuthState,
      userState: { ...mockAuthState.userState, roleType },
    });
    fixture.detectChanges();

    const surplus = fixture.nativeElement.querySelector('[data-testid="surplus"]');
    expect(surplus.textContent).toContain('View history');
    expect(surplus.textContent).not.toContain('Change');
    expect(surplus.querySelector('a[href="/edit-surplus"]')).toBeNull();
    expect(surplus.querySelector('a[href="/surplus-history"]')).toBeTruthy();
  });

  it.each(['CCA_2', undefined] as const)('should hide Change for surplus with scheme version %s', (schemeVersion) => {
    facilityDetails.surplusDetails = { schemeVersion, surplusGained: '1627', hasHistory: true };
    fixture.destroy();
    fixture = TestBed.createComponent(FacilityDetailsComponent);
    fixture.detectChanges();

    const surplus = fixture.nativeElement.querySelector('[data-testid="surplus"]');
    expect(surplus.textContent).toContain('1,627');
    expect(surplus.querySelector('a')).toBeNull();
    expect(surplus.textContent).not.toContain('View history');
    expect(surplus.textContent).not.toContain('|');
  });

  it('should display zero without a history link when no history exists', () => {
    facilityDetails.surplusDetails = { schemeVersion: 'CCA_3', surplusGained: '0', hasHistory: false };
    fixture.destroy();
    fixture = TestBed.createComponent(FacilityDetailsComponent);
    fixture.detectChanges();

    const surplus = fixture.nativeElement.querySelector('[data-testid="surplus"]');
    expect(surplus.querySelector('dd').textContent.trim()).toBe('0');
    expect(surplus.textContent).not.toContain('View history');
    expect(surplus.textContent).toContain('Change');
  });

  it.each(['SECTOR_USER', 'OPERATOR'] as const)('should hide View history for %s without history', (roleType) => {
    facilityDetails.surplusDetails.hasHistory = false;
    TestBed.inject(AuthStore).setState({
      ...mockAuthState,
      userState: { ...mockAuthState.userState, roleType },
    });
    fixture.destroy();
    fixture = TestBed.createComponent(FacilityDetailsComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="surplus"] a')).toBeNull();
  });

  it('should handle facility details without surplus data', () => {
    facilityDetails.surplusDetails = undefined;
    fixture.destroy();
    fixture = TestBed.createComponent(FacilityDetailsComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="surplus"]')).toBeNull();
  });

  it('should display every digit of a surplus above the safe integer limit', () => {
    facilityDetails.surplusDetails.surplusGained = '9007199254740993';
    fixture.destroy();
    fixture = TestBed.createComponent(FacilityDetailsComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="surplus"] dd').textContent.trim()).toBe(
      '9,007,199,254,740,993',
    );
  });
});
