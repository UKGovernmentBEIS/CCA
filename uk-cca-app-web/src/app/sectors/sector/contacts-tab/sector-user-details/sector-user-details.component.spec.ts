import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { screen } from '@testing-library/dom';

import { mockSectorUserDetails } from '../../../specs/fixtures/mock';
import { ActiveSectorUserStore } from '../active-sector-user.store';
import { SectorUserDetailsComponent } from './sector-user-details.component';

describe('SectorUserDetailsComponent', () => {
  let component: SectorUserDetailsComponent;
  let fixture: ComponentFixture<SectorUserDetailsComponent>;
  let store: ActiveSectorUserStore;
  let authStore: AuthStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorUserDetailsComponent],
      providers: [
        ActiveSectorUserStore,
        AuthStore,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: jest.fn().mockReturnValue(1) } },
          },
        },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveSectorUserStore);
    store.setState({ details: mockSectorUserDetails, editable: true });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      status: 'ENABLED',
      roleType: 'REGULATOR',
      userId: '5reg',
    });

    fixture = TestBed.createComponent(SectorUserDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("should display the user's name as title", () => {
    expect(screen.getByText('sector user')).toBeInTheDocument();
  });

  it("should display the sections' titles", () => {
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Organisation details')).toBeInTheDocument();
    expect(screen.getByText('Credentials')).toBeInTheDocument();
  });

  it('should display available "change" links', () => {
    const changeLinks = screen.getAllByRole('link').filter((l) => l.textContent === 'Change');
    expect(changeLinks.length).toEqual(7);
  });
});
