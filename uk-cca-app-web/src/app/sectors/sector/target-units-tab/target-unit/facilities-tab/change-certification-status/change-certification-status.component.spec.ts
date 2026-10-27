import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of } from 'rxjs';

import { click, getByLabelText, getByRole, getByText } from '@testing';

import { UpdateFacilityDataService } from 'cca-api';

import { mockFacilityCertifications, mockFacilityDetails } from '../testing/mock-data';
import { ChangeCertificationStatusComponent } from './change-certification-status.component';

describe('ChangeCertificationStatusComponent', () => {
  let component: ChangeCertificationStatusComponent;
  let fixture: ComponentFixture<ChangeCertificationStatusComponent>;
  let router: Router;
  const updateFacilityCertificationStatus = vi.fn(() => of(undefined));

  const createComponent = async (certificationPeriod: 'CP6' | 'CP7' | 'CP8') => {
    const activatedRouteStub = {
      snapshot: {
        paramMap: convertToParamMap({ facilityId: `${mockFacilityDetails.facilityId}`, certificationPeriod }),
        data: {
          facilityDetails: mockFacilityDetails,
          certificationDetails: mockFacilityCertifications.find((c) => c.certificationPeriod === certificationPeriod),
        },
      },
    } as unknown as ActivatedRoute;

    await TestBed.configureTestingModule({
      imports: [ChangeCertificationStatusComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: UpdateFacilityDataService, useValue: { updateFacilityCertificationStatus } },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(ChangeCertificationStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(() => updateFacilityCertificationStatus.mockClear());

  describe('for CP6', () => {
    beforeEach(() => createComponent('CP6'));

    it('should display the certification period duration', () => {
      expect(getByRole('heading', { name: 'Certification period 6 duration' })).toBeTruthy();
      expect(getByText('1 Jul 2023 to 30 Jun 2025')).toBeTruthy();
    });

    it('should display the return link to the facility', () => {
      expect(getByRole('link', { name: /^Return to:/ })).toBeTruthy();
    });

    it('should initialise form as valid when status is CERTIFIED and startDate is prepopulated', () => {
      expect(component.form.controls.certificationStatus.value).toBe('CERTIFIED');
      expect(component.form.controls.startDate.value).toBeInstanceOf(Date);
      expect(component.form.valid).toBe(true);
    });

    it('should require startDate when status is CERTIFIED', () => {
      component.form.controls.startDate.setValue(null);

      expect(component.form.controls.startDate.errors?.['required']).toBeDefined();
    });

    it('should clear startDate errors when status changes to DECERTIFIED', () => {
      component.form.controls.certificationStatus.setValue('DECERTIFIED');
      fixture.detectChanges();

      expect(component.form.controls.startDate.errors).toBeNull();
      expect(component.form.valid).toBe(true);
    });

    it('should submit the CP6 status and return to the certification status tab', () => {
      click(getByRole('button', { name: 'Save and continue' }));

      expect(updateFacilityCertificationStatus).toHaveBeenCalledWith(mockFacilityDetails.facilityId, {
        certificationStatus: 'CERTIFIED',
        certificationPeriodId: 1,
        startDate: '2024-12-12',
      });
      expect(router.navigate).toHaveBeenCalledWith(
        ['../..'],
        expect.objectContaining({ fragment: 'certification-status' }),
      );
    });

    it('should reject a start date outside the certification period', () => {
      component.form.controls.startDate.setValue(new Date(Date.UTC(2026, 0, 1)));

      expect(component.form.controls.startDate.errors?.['invalidDateRange']).toBe(
        'The start date must be between 1 Jul 2023 and 30 Jun 2025',
      );
    });

    it('should decertify even when the hidden start date has been cleared', () => {
      component.form.controls.startDate.setValue(null);
      click(getByLabelText('Decertified'));
      click(getByRole('button', { name: 'Save and continue' }));

      expect(updateFacilityCertificationStatus).toHaveBeenCalledWith(mockFacilityDetails.facilityId, {
        certificationStatus: 'DECERTIFIED',
        certificationPeriodId: 1,
        startDate: null,
      });
    });
  });

  describe('for CP7', () => {
    beforeEach(() => createComponent('CP7'));

    it('should display the certification period duration', () => {
      expect(getByText('Certification period 7 duration')).toBeTruthy();
      expect(getByText('1 Jul 2025 to 30 Jun 2027')).toBeTruthy();
    });

    it('should not submit when no status is selected', () => {
      click(getByRole('button', { name: 'Save and continue' }));
      fixture.detectChanges();

      expect(updateFacilityCertificationStatus).not.toHaveBeenCalled();
      expect(
        getByText('Select a certification status', fixture.nativeElement.querySelector('govuk-error-summary')),
      ).toBeTruthy();
    });

    it('should submit the CP7 status without a start date when decertified', () => {
      click(getByLabelText('Decertified'));
      click(getByRole('button', { name: 'Save and continue' }));

      expect(updateFacilityCertificationStatus).toHaveBeenCalledWith(mockFacilityDetails.facilityId, {
        certificationStatus: 'DECERTIFIED',
        certificationPeriodId: 2,
        startDate: null,
      });
    });
  });

  describe('for CP8', () => {
    beforeEach(() => createComponent('CP8'));

    it('should display the certification period duration and hint', () => {
      expect(getByText('Certification period 8 duration')).toBeTruthy();
      expect(getByText('1 Jul 2027 to 30 Jun 2029')).toBeTruthy();
      expect(
        getByText('This option will determine whether this facility will be certified until the end of CP8 or not.'),
      ).toBeTruthy();
    });

    it('should submit the certification period id received from the api', () => {
      click(getByLabelText('Decertified'));
      click(getByRole('button', { name: 'Save and continue' }));

      expect(updateFacilityCertificationStatus).toHaveBeenCalledWith(mockFacilityDetails.facilityId, {
        certificationStatus: 'DECERTIFIED',
        certificationPeriodId: 3,
        startDate: null,
      });
    });
  });
});
