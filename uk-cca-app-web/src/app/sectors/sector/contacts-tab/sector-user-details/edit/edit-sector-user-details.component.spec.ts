import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByText, setInputValue } from '@testing';
import { Mocked } from 'vitest';

import { SectorUsersService } from 'cca-api';

import { mockSectorUserDetails } from '../../../../specs/fixtures/mock';
import { ActiveSectorUserStore } from '../../active-sector-user.store';
import { EditSectorUserDetailsComponent } from './edit-sector-user-details.component';

describe('EditSectorUserDetailsComponent', () => {
  let component: EditSectorUserDetailsComponent;
  let fixture: ComponentFixture<EditSectorUserDetailsComponent>;
  let store: ActiveSectorUserStore;
  let router: Router;

  let sectorUsersServiceMock: Mocked<Partial<SectorUsersService>>;

  beforeEach(async () => {
    sectorUsersServiceMock = {
      updateCurrentSectorUser: vi.fn().mockReturnValue(of(null)),
      updateSectorUserBySectorAssociationIdAndUserId: vi.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      imports: [EditSectorUserDetailsComponent],
      providers: [
        ActiveSectorUserStore,
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ sectorId: '4321', sectorUserId: '1234' }),
        },
        { provide: SectorUsersService, useValue: sectorUsersServiceMock },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveSectorUserStore);
    store.setState({ details: mockSectorUserDetails, editable: true });

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(EditSectorUserDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct title', () => {
    expect(getByText('Change user details')).toBeTruthy();
  });

  it('should display all form fields', () => {
    expect(getByText('First name')).toBeTruthy();
    expect(getByText('Last name')).toBeTruthy();
    expect(getByText('Job title (optional)')).toBeTruthy();
    expect(getByText('Email address')).toBeTruthy();
    expect(getByText('Contact type')).toBeTruthy();
    expect(getByText('Organisation name (optional)')).toBeTruthy();
    expect(getByText('Phone number (optional)')).toBeTruthy();
  });

  it('should update and submit sector user details', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    const firstNameInput = fixture.nativeElement.querySelector('div[formcontrolname="firstName"] input');
    const lastNameInput = fixture.nativeElement.querySelector('div[formcontrolname="lastName"] input');

    expect(firstNameInput).toBeTruthy();
    expect(lastNameInput).toBeTruthy();

    setInputValue(firstNameInput, 'reg1');
    setInputValue(lastNameInput, 'basic1');

    fixture.detectChanges();
    const confirmBtn = getByText('Confirm and continue');
    click(confirmBtn);
    fixture.detectChanges();

    expect(sectorUsersServiceMock.updateSectorUserBySectorAssociationIdAndUserId).toHaveBeenCalledWith(4321, '1234', {
      contactType: 'SECTOR_ASSOCIATION',
      email: 'sector-ser@cca.uk',
      firstName: 'reg1',
      jobTitle: null,
      lastName: 'basic1',
      mobileNumber: {
        countryCode: '44',
        number: '7123456789',
      },
      organisationName: 'org',
      phoneNumber: {
        countryCode: '44',
        number: '7123456789',
      },
    });

    expect(navigateSpy).toHaveBeenCalled();
  });
});
