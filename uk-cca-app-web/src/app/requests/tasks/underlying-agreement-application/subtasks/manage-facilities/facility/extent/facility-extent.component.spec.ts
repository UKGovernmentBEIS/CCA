import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, MockType } from '@netz/common/testing';
import { FACILITY_EXTENT_FORM, TasksApiService, underlyingAgreementQuery } from '@requests/common';

import { FacilityExtentComponent } from './facility-extent.component';

describe('FacilityExtentComponent', () => {
  let component: FacilityExtentComponent;
  let fixture: ComponentFixture<FacilityExtentComponent>;
  let tasksApiService: MockType<TasksApiService>;

  // `facilityId` is a route param: the component reads it from `snapshot.params`.
  const route = new ActivatedRouteStub({ facilityId: 'ADS_1-F00001' });

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: vi.fn().mockReturnValue(
        of({
          underlyingAgreement: {
            facilities: [
              {
                facilityId: 'ADS_1-F00001',
                facilityDetails: { name: 'Test Facility' },
                facilityContact: {},
                eligibilityDetailsAndAuthorisation: {},
                facilityExtent: {},
                apply70Rule: {},
              },
            ],
          },
        }),
      ),
    };

    // Mock RequestTaskStore with pre-configured data
    const mockStore = {
      select: vi.fn().mockImplementation((selector) => {
        if (selector === requestTaskQuery.selectRequestTaskPayload) {
          return signal({
            underlyingAgreement: {
              facilities: [
                {
                  facilityId: 'ADS_1-F00001',
                  facilityDetails: { name: 'Test Facility' },
                  facilityExtent: {
                    areActivitiesClaimed: true,
                    manufacturingProcessFile: '5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e',
                    processFlowFile: '6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f',
                    annotatedSitePlansFile: '7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a',
                    eligibleProcessFile: '8e9f0a1b-2c3d-4e5f-6a7b-8c9d0e1f2a3b',
                    activitiesDescriptionFile: '9f0a1b2c-3d4e-5f6a-7b8c-9d0e1f2a3b4c',
                  },
                },
              ],
            },
          });
        }

        if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
        if (selector === requestTaskQuery.selectRequestTaskId) return signal(123);
        if (selector === requestTaskQuery.selectRequestTaskType)
          return signal('UNDERLYING_AGREEMENT_APPLICATION_SUBMIT');

        if (selector === underlyingAgreementQuery.selectAttachments) {
          return signal({
            '5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e': 'manufacturingProcessFile.xlsx',
            '6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f': 'processFlowFile.xlsx',
            '7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a': 'annotatedSitePlansFile.xlsx',
            '8e9f0a1b-2c3d-4e5f-6a7b-8c9d0e1f2a3b': 'eligibleProcessFile.xlsx',
            '9f0a1b2c-3d4e-5f6a-7b8c-9d0e1f2a3b4c': 'activitiesDescriptionFile.xlsx',
          });
        }

        if (selector === underlyingAgreementQuery.selectFacility('ADS_1-F00001')) {
          return signal({
            facilityId: 'ADS_1-F00001',
            facilityDetails: { name: 'Test Facility' },
            facilityContact: {},
            eligibilityDetailsAndAuthorisation: {},
            facilityExtent: {
              areActivitiesClaimed: true,
              manufacturingProcessFile: '5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e',
              processFlowFile: '6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f',
              annotatedSitePlansFile: '7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a',
              eligibleProcessFile: '8e9f0a1b-2c3d-4e5f-6a7b-8c9d0e1f2a3b',
              activitiesDescriptionFile: '9f0a1b2c-3d4e-5f6a-7b8c-9d0e1f2a3b4c',
            },
            apply70Rule: {},
          });
        }

        return signal({});
      }),
    };

    const fb = new FormBuilder();
    const mockForm = fb.group({
      manufacturingProcessFile: fb.control({
        file: { name: 'manufacturingProcessFile.xlsx' },
        uuid: '5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e',
      }),
      processFlowFile: fb.control({
        file: { name: 'processFlowFile.xlsx' },
        uuid: '6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f',
      }),
      annotatedSitePlansFile: fb.control({
        file: { name: 'annotatedSitePlansFile.xlsx' },
        uuid: '7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a',
      }),
      eligibleProcessFile: fb.control({
        file: { name: 'eligibleProcessFile.xlsx' },
        uuid: '8e9f0a1b-2c3d-4e5f-6a7b-8c9d0e1f2a3b',
      }),
      areActivitiesClaimed: fb.control(true),
      activitiesDescriptionFile: fb.control({
        file: { name: 'activitiesDescriptionFile.xlsx' },
        uuid: '9f0a1b2c-3d4e-5f6a-7b8c-9d0e1f2a3b4c',
      }),
    });

    TestBed.configureTestingModule({
      imports: [FacilityExtentComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: RequestTaskStore, useValue: mockStore },
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: FACILITY_EXTENT_FORM, useValue: mockForm },
      ],
    })
      .overrideComponent(FacilityExtentComponent, {
        set: {
          providers: [],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(FacilityExtentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });

  // The conditional activities description file is enabled/disabled while the user toggles
  // "are activities claimed". Changing the state of a form control inside a `computed` writes to the
  // signals of the file input's `setDisabledState`, which Angular rejects with NG0600.
  describe('activities description file', () => {
    it('should disable and clear the file when activities are not claimed', () => {
      component['form'].get('areActivitiesClaimed').setValue(false);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(component['form'].get('activitiesDescriptionFile').disabled).toBeTruthy();
      expect(component['form'].get('activitiesDescriptionFile').value).toBeNull();
    });

    it('should enable the file when activities are claimed again', () => {
      component['form'].get('areActivitiesClaimed').setValue(false);
      fixture.detectChanges();

      component['form'].get('areActivitiesClaimed').setValue(true);

      expect(() => fixture.detectChanges()).not.toThrow();

      expect(component['form'].get('activitiesDescriptionFile').enabled).toBeTruthy();
    });
  });

  // The payload used to fall back to a stored uuid when the control was empty, so a stored file
  // survived a cleared control. The wizard only submits a valid form, so the reachable clear is the
  // conditional activities description file that the effect disables; both are pinned here.
  describe('clearing an uploaded file', () => {
    beforeEach(() => {
      // The submit callback navigates once the task is saved; the tests only care about the payload.
      vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    });

    it('should not restore the stored uuid of an empty file control', () => {
      component['form'].get('manufacturingProcessFile').setValue(null);
      fixture.detectChanges();

      component['onSubmit']();

      expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
        expect.objectContaining({
          requestTaskActionPayload: expect.objectContaining({
            underlyingAgreement: expect.objectContaining({
              facilities: [
                expect.objectContaining({
                  facilityExtent: expect.objectContaining({
                    manufacturingProcessFile: '',
                    processFlowFile: '6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f',
                  }),
                }),
              ],
            }),
          }),
        }),
      );
    });

    it('should clear the activities description file when the activities are not claimed', () => {
      component['form'].get('areActivitiesClaimed').setValue(false);
      fixture.detectChanges();

      component['onSubmit']();

      expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
        expect.objectContaining({
          requestTaskActionPayload: expect.objectContaining({
            underlyingAgreement: expect.objectContaining({
              facilities: [
                expect.objectContaining({
                  facilityExtent: expect.objectContaining({
                    activitiesDescriptionFile: '',
                    manufacturingProcessFile: '5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e',
                  }),
                }),
              ],
            }),
          }),
        }),
      );
    });
  });
});
