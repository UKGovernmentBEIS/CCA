import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByLabelText, getByText } from '@testing';

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
    expect(getByText('Change')).toBeTruthy();
    expect(getByText('Scheme exit date')).toBeTruthy();

    expect(
      getByText(
        'The scheme exit date determines whether the operator of this facility will be charged for the current charging period of subsistence fees.',
      ),
    ).toBeTruthy();

    expect(
      getByText(
        `When a facility left the scheme before the current charging period, but the variation to remove that facility or a termination workflow hasn't completed, you can manually set the scheme exit date by selecting Yes.`,
      ),
    ).toBeTruthy();

    expect(getByText('Did the facility exit the scheme before the start of the current charging year?')).toBeTruthy();
  });

  it('should show scheme exit date input when "Yes, set the scheme exit date"', () => {
    click(getByLabelText('Yes, set scheme exit date'));

    const dateInput = document.getElementById('schemeExitDate') as HTMLElement;
    expect(dateInput).toBeTruthy();
    expect(getByLabelText('Day')).toBeTruthy();
    expect(getByLabelText('Month')).toBeTruthy();
    expect(getByLabelText('Year')).toBeTruthy();
  });
});
