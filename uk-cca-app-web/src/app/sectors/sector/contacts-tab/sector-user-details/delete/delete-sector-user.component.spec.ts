import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthService } from '@shared/services';
import { getByRole, getByText } from '@testing';

import { mockSectorDetails, mockSectorUserDetails, mockSubSectorDetails } from '../../../../specs/fixtures/mock';
import { DeleteSectorUserComponent } from './delete-sector-user.component';

describe('DeleteSectorUserComponent', () => {
  let component: DeleteSectorUserComponent;
  let fixture: ComponentFixture<DeleteSectorUserComponent>;

  let authService: Partial<jest.Mocked<AuthService>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteSectorUserComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: authService,
        },
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              details: { sectorAssociationInfo: mockSectorDetails },
              sectorUserDetails: mockSubSectorDetails,
            }),
            fragment: of({}),
            snapshot: {
              paramMap: { get: jest.fn().mockReturnValue({ id: 1, sectorUserId: 1 }) },
              data: { sectorUserDetails: mockSectorUserDetails },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteSectorUserComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct header', () => {
    expect(getByText('Confirm that the user account of sector user will be removed from this sector')).toBeTruthy();
  });

  it('should display correct warning', () => {
    expect(getByText('You will not be able to undo this action.')).toBeTruthy();
  });

  it('should display correct deletion info', () => {
    expect(
      getByText(
        "All tasks currently assigned to this user will be automatically unassigned after you select 'Confirm removal'.",
      ),
    ).toBeTruthy();

    expect(
      getByText("If you need to reassign any of these tasks before removing this user, select 'Cancel'."),
    ).toBeTruthy();
  });

  it('should display "Confirm removal" button and "Cancel" link', () => {
    expect(getByRole('button')).toBeTruthy();
    expect(getByRole('button').textContent).toBe('Confirm removal');

    expect(getByRole('link')).toBeTruthy();
    expect(getByRole('link').textContent).toBe('Cancel');
  });
});
