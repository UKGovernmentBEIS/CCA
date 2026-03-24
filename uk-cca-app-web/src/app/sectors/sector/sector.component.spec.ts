import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getAllByRole, getByText } from '@testing';

import { mockSectorDetails } from '../specs/fixtures/mock';
import { ActiveSectorStore } from './active-sector.store';
import { SectorComponent } from './sector.component';

describe('SectorComponent', () => {
  let fixture: ComponentFixture<SectorComponent>;
  let store: ActiveSectorStore;
  let authStore: AuthStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorComponent],
      providers: [
        ActiveSectorStore,
        AuthStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ sectorId: 1 }) },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveSectorStore);
    store.setState(mockSectorDetails);

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR', status: 'ENABLED', userId: '12345' });

    fixture = TestBed.createComponent(SectorComponent);
    fixture.detectChanges();
  });

  it('should render title', () => {
    const title = `${mockSectorDetails.sectorAssociationDetails.acronym} - ${mockSectorDetails.sectorAssociationDetails.commonName}`;

    expect(getByText(title)).toBeTruthy();
  });

  it('should contain tabs "Details", "Scheme", "Contacts", "Target units", "Workflow history", "Reports" and "Subsistence fees"', () => {
    const tabTitles = [
      'Details',
      'Scheme',
      'Contacts',
      'Target units',
      'Workflow history',
      'Reports',
      'Subsistence fees',
    ];

    const tabs = getAllByRole('tab');
    expect(tabs.length).toBe(7);
    expect(tabs[0].textContent?.trim()).toBe(tabTitles[0]);
    expect(tabs[1].textContent?.trim()).toBe(tabTitles[1]);
    expect(tabs[2].textContent?.trim()).toBe(tabTitles[2]);
    expect(tabs[3].textContent?.trim()).toBe(tabTitles[3]);
    expect(tabs[4].textContent?.trim()).toBe(tabTitles[4]);
    expect(tabs[5].textContent?.trim()).toBe(tabTitles[5]);
    expect(tabs[6].textContent?.trim()).toBe(tabTitles[6]);
  });
});
