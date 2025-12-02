import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { mockFacilityDetails } from '../testing/mock-data';
import EditFacilityDetailsComponent from './edit-facility-details.component';

describe('EditFacilityDetailsComponent', () => {
  let component: EditFacilityDetailsComponent;
  let fixture: ComponentFixture<EditFacilityDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditFacilityDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            facilityDetails: mockFacilityDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditFacilityDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render all text info', () => {
    expect(screen.getByText('Change')).toBeInTheDocument();
    expect(screen.getByText('Scheme exit date')).toBeInTheDocument();

    expect(
      screen.getByText(
        'The scheme exit date determines whether the operator of this facility will be charged for the current charging period of subsistence fees.',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        `When a facility left the scheme before the current charging period, but the variation to remove that facility or a termination workflow hasn't completed, you can manually set the scheme exit date by selecting Yes.`,
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText('Did the facility exit the scheme before the start of the current charging year?'),
    ).toBeInTheDocument();
  });

  it('should show scheme exit date input when "Yes, set the scheme exit date"', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByLabelText('Yes, set scheme exit date'));

    expect(document.getElementById('schemeExitDate')).toBeVisible();
    expect(screen.getByLabelText('Day')).toBeVisible();
    expect(screen.getByLabelText('Month')).toBeVisible();
    expect(screen.getByLabelText('Year')).toBeVisible();
  });
});
