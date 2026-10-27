import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { getAllByRole, getSummaryListData, queryByRole } from '@testing';

import { FacilityInfoViewService } from 'cca-api';

import { mockFacilityCertifications } from '../testing/mock-data';
import { CertificationStatusTabComponent } from './certification-status-tab.component';

describe('CertificationStatusTabComponent', () => {
  let element: HTMLElement;
  const getFacilityCertifications = vi.fn(() => of(mockFacilityCertifications));

  const createComponent = async (roleType: 'REGULATOR' | 'OPERATOR' | 'SECTOR_USER') => {
    TestBed.inject(AuthStore).setUserState({ roleType, status: 'ENABLED' });
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/1', CertificationStatusTabComponent);
    element = harness.routeNativeElement;
  };

  beforeEach(async () => {
    getFacilityCertifications.mockClear();

    await TestBed.configureTestingModule({
      imports: [CertificationStatusTabComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        provideRouter([{ path: ':facilityId', component: CertificationStatusTabComponent }]),
        { provide: FacilityInfoViewService, useValue: { getFacilityCertifications } },
      ],
    }).compileComponents();
  });

  it('should fetch the certifications of the facility', async () => {
    await createComponent('REGULATOR');

    expect(getFacilityCertifications).toHaveBeenCalledWith(1);
  });

  it('should render the CCA3 scheme before the CCA2 scheme', async () => {
    await createComponent('REGULATOR');

    expect(getAllByRole('heading', {}, element).map((heading) => heading.textContent.trim())).toEqual([
      'CCA3 certification status',
      'CCA2 certification status',
    ]);
  });

  it('should render a list per certification period in the order received', async () => {
    await createComponent('REGULATOR');

    expect(getSummaryListData(element)).toEqual([
      [
        ['Certification period', 'Certification duration', 'Current status'],
        ['CP8', '1 Jul 2027 to 30 Jun 2029', 'Not yet defined'],
      ],
      [
        ['Certification period', 'Certification duration', 'Current status'],
        ['CP7', '1 Jul 2025 to 30 Jun 2027', 'Not yet defined'],
      ],
      [
        ['Certification period', 'Certification duration', 'Current status'],
        ['CP6', '1 Jul 2023 to 30 Jun 2025', 'Certified'],
      ],
    ]);
  });

  it('should show Change links for a regulator', async () => {
    await createComponent('REGULATOR');

    const links = getAllByRole('link', { name: /^Change/ }, element).map((link) => link.getAttribute('href'));
    expect(links).toEqual([
      '/1/CP8/change-certification-status',
      '/1/CP7/change-certification-status',
      '/1/CP6/change-certification-status',
    ]);
  });

  it('should name each Change link after its certification period', async () => {
    await createComponent('REGULATOR');

    expect(
      getAllByRole('link', { name: /^Change/ }, element).map((link) => link.textContent.replace(/\s+/g, ' ').trim()),
    ).toEqual(['Change CP8 current status', 'Change CP7 current status', 'Change CP6 current status']);
  });

  it('should omit a scheme without certification periods', async () => {
    getFacilityCertifications.mockReturnValueOnce(
      of(mockFacilityCertifications.filter((c) => c.schemeVersion === 'CCA_2')),
    );
    await createComponent('REGULATOR');

    expect(queryByRole('heading', { name: 'CCA3 certification status' }, element)).toBeNull();
    expect(getAllByRole('heading', {}, element)).toHaveLength(1);
  });

  it.each(['OPERATOR', 'SECTOR_USER'] as const)('should hide Change links for %s', async (roleType) => {
    await createComponent(roleType);

    expect(queryByRole('link', { name: /^Change/ }, element)).toBeNull();
  });
});
