import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService, underlyingAgreementQuery } from '@requests/common';

import { UnderlyingAgreementSaveRequestTaskActionPayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { FacilityEligibilityDetailsComponent } from './facility-eligibility-details.component';

describe('FacilityEligibilityDetailsComponent', () => {
  let fixture: ComponentFixture<FacilityEligibilityDetailsComponent>;
  let store: RequestTaskStore;

  const facilityId = 'ADS_1-F00001';
  // `facilityId` is a route param: the component reads it from `snapshot.params`.
  const route = new ActivatedRouteStub({ facilityId });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityEligibilityDetailsComponent, RouterModule.forRoot([])],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
      ],
    })
      .overrideComponent(FacilityEligibilityDetailsComponent, {
        set: {
          changeDetection: ChangeDetectionStrategy.Default,
        },
      })
      .compileComponents();

    store = TestBed.inject(RequestTaskStore);

    // Mock store select methods
    vi.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal({
          underlyingAgreement: {
            facilities: [
              {
                facilityId,
                facilityDetails: { name: 'Test Facility' },
                eligibilityDetailsAndAuthorisation: {
                  isConnectedToExistingFacility: true,
                  adjacentFacilityId: 'ADS_1-F11111',
                  agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
                  erpAuthorisationExists: true,
                  authorisationNumber: 'authorisation',
                  regulatorName: 'ENVIRONMENT_AGENCY',
                  permitFile: 'test-uuid',
                },
              },
            ],
          },
        });
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(123);

      if (selector === underlyingAgreementQuery.selectFacility(facilityId)) {
        return signal({
          facilityId,
          facilityDetails: { name: 'Test Facility' },
          eligibilityDetailsAndAuthorisation: {
            isConnectedToExistingFacility: true,
            adjacentFacilityId: 'ADS_1-F11111',
            agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
            erpAuthorisationExists: true,
            authorisationNumber: 'authorisation',
            regulatorName: 'ENVIRONMENT_AGENCY',
            permitFile: 'test-uuid',
          },
        });
      }

      if (selector === underlyingAgreementQuery.selectAttachments) return signal([]);
      if (selector === requestTaskQuery.selectRequestTaskType) return signal('UNDERLYING_AGREEMENT_APPLICATION');

      return signal({});
    });

    fixture = TestBed.createComponent(FacilityEligibilityDetailsComponent);
    fixture.detectChanges();
  });

  it('should test that the form renders properly', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });

  // The conditional permit file is enabled/disabled while the user changes the agreement type and the
  // erp authorisation flag. Changing the state of a form control inside a `computed` writes to the
  // signals of the file input's `setDisabledState`, which Angular rejects with NG0600.
  describe('conditional permit file', () => {
    const showPermitFile = () => {
      const form = fixture.componentInstance['form'];
      form.get('agreementType').setValue('ENVIRONMENTAL_PERMITTING_REGULATIONS');
      form.get('erpAuthorisationExists').setValue(true);
      fixture.detectChanges();

      return form;
    };

    it('should disable and clear the permit file when the agreement is not environmental', () => {
      const form = showPermitFile();
      expect(fixture.nativeElement.querySelector('cca-file-input')).toBeTruthy();

      form.get('agreementType').setValue('ENERGY_INTENSIVE');

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(form.get('permitFile').disabled).toBeTruthy();
      expect(form.get('permitFile').value).toBeNull();
    });

    it('should disable and clear the permit file when the erp authorisation is removed', () => {
      const form = showPermitFile();

      form.get('erpAuthorisationExists').setValue(false);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(form.get('permitFile').disabled).toBeTruthy();
      expect(form.get('permitFile').value).toBeNull();
    });

    it('should enable the permit file again when the environmental agreement is restored', () => {
      const form = showPermitFile();
      form.get('agreementType').setValue('ENERGY_INTENSIVE');
      fixture.detectChanges();

      form.get('agreementType').setValue('ENVIRONMENTAL_PERMITTING_REGULATIONS');
      form.get('erpAuthorisationExists').setValue(true);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(form.get('permitFile').enabled).toBeTruthy();
    });
  });

  // `form.value` leaves disabled controls out, so a control that the effect disabled and reset must
  // not keep its previously saved value in the payload.
  it('should save the reset erp authorisation fields when the agreement type is not environmental', () => {
    const tasksApiService = TestBed.inject(TasksApiService);
    const saveSpy = vi
      .spyOn(tasksApiService, 'saveRequestTaskAction')
      .mockReturnValue(
        of({ underlyingAgreement: { facilities: [] } } as unknown as UnderlyingAgreementSubmitRequestTaskPayload),
      );
    // The submit callback navigates once the task is saved; the test only cares about the payload.
    vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    const form = fixture.componentInstance['form'];
    // A facility that was saved while it held an erp authorisation, plus the disabled facility name.
    form.patchValue({
      name: 'Test Facility',
      isConnectedToExistingFacility: true,
      adjacentFacilityId: 'ADS_1-F11111',
      agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
      erpAuthorisationExists: true,
      authorisationNumber: 'AUTH-1',
      regulatorName: 'ENVIRONMENT_AGENCY',
    });
    fixture.detectChanges();

    form.get('agreementType').setValue('ENERGY_INTENSIVE');
    fixture.detectChanges();

    expect(form.get('erpAuthorisationExists').disabled).toBe(true);
    expect(form.getRawValue().erpAuthorisationExists).toBeNull();

    fixture.componentInstance.onSubmit();

    const dto = saveSpy.mock.calls[0][0];
    const details = (dto.requestTaskActionPayload as UnderlyingAgreementSaveRequestTaskActionPayload)
      .underlyingAgreement.facilities[0].eligibilityDetailsAndAuthorisation;

    // The reset fields are saved as null, the fields the user left alone keep their values, and the
    // disabled facility name is left out.
    expect(details).toStrictEqual({
      isConnectedToExistingFacility: true,
      adjacentFacilityId: 'ADS_1-F11111',
      agreementType: 'ENERGY_INTENSIVE',
      erpAuthorisationExists: null,
      authorisationNumber: null,
      regulatorName: null,
      permitFile: null,
    });
  });

  it('should keep the erp authorisation details when the agreement type is environmental', () => {
    const tasksApiService = TestBed.inject(TasksApiService);
    const saveSpy = vi
      .spyOn(tasksApiService, 'saveRequestTaskAction')
      .mockReturnValue(
        of({ underlyingAgreement: { facilities: [] } } as unknown as UnderlyingAgreementSubmitRequestTaskPayload),
      );
    vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    const form = fixture.componentInstance['form'];
    form.patchValue({
      isConnectedToExistingFacility: true,
      agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
      erpAuthorisationExists: true,
      authorisationNumber: 'AUTH-1',
      regulatorName: 'ENVIRONMENT_AGENCY',
      permitFile: { file: new File(['test content'], 'permit.pdf'), uuid: 'permit-uuid' },
    });
    fixture.detectChanges();

    expect(form.get('authorisationNumber').enabled).toBe(true);

    fixture.componentInstance.onSubmit();

    const dto = saveSpy.mock.calls[0][0];
    const details = (dto.requestTaskActionPayload as UnderlyingAgreementSaveRequestTaskActionPayload)
      .underlyingAgreement.facilities[0].eligibilityDetailsAndAuthorisation;

    expect(details).toEqual(
      expect.objectContaining({
        agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
        erpAuthorisationExists: true,
        authorisationNumber: 'AUTH-1',
        regulatorName: 'ENVIRONMENT_AGENCY',
        permitFile: 'permit-uuid',
      }),
    );
  });
});
