import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthService } from '@shared/services';
import { screen } from '@testing-library/dom';

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
    expect(
      screen.getByText('Confirm that the user account of sector user will be removed from this sector'),
    ).toBeInTheDocument();
  });

  it('should display correct warning', () => {
    expect(screen.getByText('You will not be able to undo this action.')).toBeInTheDocument();
  });

  it('should display correct deletion info', () => {
    expect(
      screen.getByText(
        "All tasks currently assigned to this user will be automatically unassigned after you select 'Confirm removal'.",
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText("If you need to reassign any of these tasks before removing this user, select 'Cancel'."),
    ).toBeInTheDocument();
  });

  it('should display "Confirm removal" button and "Cancel" link', () => {
    expect(screen.getByRole('button')).toBeInTheDocument();
    expect(screen.getByRole('button').textContent).toBe('Confirm removal');

    expect(screen.getByRole('link')).toBeInTheDocument();
    expect(screen.getByRole('link').textContent).toBe('Cancel');
  });
});
