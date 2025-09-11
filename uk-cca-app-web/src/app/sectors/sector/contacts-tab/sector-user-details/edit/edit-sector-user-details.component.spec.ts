import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { screen } from '@testing-library/dom';

import { mockSectorUserDetails } from '../../../../specs/fixtures/mock';
import { ActiveSectorUserStore } from '../../active-sector-user.store';
import { EditSectorUserDetailsComponent } from './edit-sector-user-details.component';

describe('EditSectorUserDetailsComponent', () => {
  let component: EditSectorUserDetailsComponent;
  let fixture: ComponentFixture<EditSectorUserDetailsComponent>;
  let store: ActiveSectorUserStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditSectorUserDetailsComponent],
      providers: [
        ActiveSectorUserStore,
        provideHttpClient(),
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

    fixture = TestBed.createComponent(EditSectorUserDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct title', () => {
    expect(screen.getByText('Change user details')).toBeInTheDocument();
  });

  it('should display all form fields', () => {
    expect(screen.getByText('First name')).toBeInTheDocument();
    expect(screen.getByText('Last name')).toBeInTheDocument();
    expect(screen.getByText('Job title (optional)')).toBeInTheDocument();
    expect(screen.getByText('Email address')).toBeInTheDocument();
    expect(screen.getByText('Contact type')).toBeInTheDocument();
    expect(screen.getByText('Organisation name (optional)')).toBeInTheDocument();
    expect(screen.getByText('Phone number (optional)')).toBeInTheDocument();
  });
});
