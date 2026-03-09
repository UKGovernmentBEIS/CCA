import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import { FACILITY_EXTENT_FORM, TasksApiService, underlyingAgreementQuery } from '@requests/common';

import { FacilityExtentComponent } from './facility-extent.component';

describe('FacilityExtentComponent', () => {
  let component: FacilityExtentComponent;
  let fixture: ComponentFixture<FacilityExtentComponent>;
  let tasksApiService: MockType<TasksApiService>;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(
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
      select: jest.fn().mockImplementation((selector) => {
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
        file: { name: 'manufacturingProcessFile.xlsx' } as File,
        uuid: '5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e',
      }),
      processFlowFile: fb.control({
        file: { name: 'processFlowFile.xlsx' } as File,
        uuid: '6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f',
      }),
      annotatedSitePlansFile: fb.control({
        file: { name: 'annotatedSitePlansFile.xlsx' } as File,
        uuid: '7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a',
      }),
      eligibleProcessFile: fb.control({
        file: { name: 'eligibleProcessFile.xlsx' } as File,
        uuid: '8e9f0a1b-2c3d-4e5f-6a7b-8c9d0e1f2a3b',
      }),
      areActivitiesClaimed: fb.control(true),
      activitiesDescriptionFile: fb.control({
        file: { name: 'activitiesDescriptionFile.xlsx' } as File,
        uuid: '9f0a1b2c-3d4e-5f6a-7b8c-9d0e1f2a3b4c',
      }),
    });

    TestBed.configureTestingModule({
      imports: [FacilityExtentComponent],
      providers: [
        provideHttpClient(),
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
    expect(fixture).toMatchSnapshot();
  });
});
