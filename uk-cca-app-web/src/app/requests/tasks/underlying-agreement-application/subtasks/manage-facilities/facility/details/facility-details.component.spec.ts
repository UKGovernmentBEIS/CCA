import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DestroyRef, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import {
  FACILITY_DETAILS_FORM,
  mockTargetUnitDetails,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';

import { FacilityService } from 'cca-api';

import { FacilityDetailsComponent } from './facility-details.component';

describe('FacilityDetailsComponent', () => {
  let fixture: ComponentFixture<FacilityDetailsComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  const route = {
    snapshot: {
      params: { facilityId: 'ADS_1-F00001' },
      pathFromRoot: [],
    },
  };

  const facilityAddress = {
    line1: 'Facility Line1',
    line2: 'Facility Line2',
    city: 'Facility City',
    postcode: 'Facility 14',
    country: 'GR',
  };

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
    };

    const destroyRef = { onDestroy: jest.fn() } as unknown as DestroyRef;
    const facilityService = {} as FacilityService;

    const formBuilder = new FormBuilder();

    // Create a test form directly
    const testForm = formBuilder.group({
      facilityId: ['ADS_1-F00001'],
      name: ['Facility 1'],
      isCoveredByUkets: [false],
      uketsId: [''],
      applicationReason: ['NEW_AGREEMENT'],
      participatingSchemeVersions: [['CCA_3']],
      previousFacilityId: [''],
      sameAddress: [[false]],
      facilityAddress: formBuilder.group({
        line1: [facilityAddress.line1],
        line2: [facilityAddress.line2],
        city: [facilityAddress.city],
        county: [''],
        postcode: [facilityAddress.postcode],
        country: [facilityAddress.country],
      }),
    });

    TestBed.configureTestingModule({
      imports: [FacilityDetailsComponent, ReactiveFormsModule, RouterModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: DestroyRef, useValue: destroyRef },
        { provide: FacilityService, useValue: facilityService },
        { provide: FACILITY_DETAILS_FORM, useValue: testForm },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    // Mock store select methods
    jest.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal({
          underlyingAgreement: {
            facilities: [
              {
                facilityId: 'ADS_1-F00001',
                facilityDetails: {
                  name: 'Facility 1',
                  isCoveredByUkets: false,
                  applicationReason: 'NEW_AGREEMENT',
                  facilityAddress: facilityAddress,
                },
              },
            ],
          },
        });
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(123);

      if (selector === underlyingAgreementQuery.selectFacility('ADS_1-F00001')) {
        return signal({
          facilityId: 'ADS_1-F00001',
          facilityDetails: {
            name: 'Facility 1',
            isCoveredByUkets: false,
            applicationReason: 'NEW_AGREEMENT',
            facilityAddress: facilityAddress,
          },
        });
      }

      if (selector === underlyingAgreementQuery.selectAccountReferenceData)
        return signal({ targetUnitAccountDetails: mockTargetUnitDetails });

      if (selector === underlyingAgreementQuery.selectAccountReferenceDataTargetUnitDetails)
        return signal({ address: mockTargetUnitDetails.address });

      return signal({});
    });

    fixture = TestBed.createComponent(FacilityDetailsComponent);
    fixture.detectChanges();
  });

  it('should render the form with the correct initial values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
