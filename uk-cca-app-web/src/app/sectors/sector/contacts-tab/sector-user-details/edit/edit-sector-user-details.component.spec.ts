import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { getByText } from '@testing';

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
});
