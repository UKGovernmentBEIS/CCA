import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { FacilityDetailsFormModel, mockRequestTaskState, TasksApiService } from '@requests/common';
import { AccountAddressFormModel } from '@shared/components';

import { FacilityService } from 'cca-api';

import { AddFacilityComponent } from './add-facility.component';

describe('AddFacilityComponent', () => {
  let component: AddFacilityComponent;
  let fixture: ComponentFixture<AddFacilityComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let facilityService: FacilityService;
  let tasksApiService: TasksApiService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddFacilityComponent],
      providers: [
        RequestTaskStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: FacilityService,
          useValue: { generateFacilityId: jest.fn() },
        },
        {
          provide: TasksApiService,
          useValue: { saveRequestTaskAction: jest.fn() },
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: { facilityId: 'ADS_F00001' },
              pathFromRoot: [
                { url: [{ path: 'requests' }] },
                { url: [{ path: 'tasks' }] },
                { url: [{ path: 'add-facility' }] },
              ],
            },
            parent: {
              snapshot: { pathFromRoot: [{ url: [{ path: 'requests' }] }, { url: [{ path: 'tasks' }] }] },
              parent: { snapshot: { pathFromRoot: [{ url: [{ path: 'requests' }] }] }, parent: null },
            },
          },
        },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    facilityService = TestBed.inject(FacilityService);
    tasksApiService = TestBed.inject(TasksApiService);

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(AddFacilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle form submission and navigate to contact details', () => {
    const facilityId = 'TEST_F00001';
    jest.spyOn(facilityService, 'generateFacilityId').mockReturnValue(of({ facilityId }));
    jest.spyOn(tasksApiService, 'saveRequestTaskAction').mockReturnValue(of({}));
    jest.spyOn(router, 'navigate');

    component.onSubmit(
      new FormGroup<FacilityDetailsFormModel>({
        name: new FormControl(null),
        facilityId: new FormControl(null),
        isCoveredByUkets: new FormControl(null),
        uketsId: new FormControl(null),
        applicationReason: new FormControl(null),
        previousFacilityId: new FormControl(null),
        participatingSchemeVersions: new FormControl(null),
        schemeParticipationChoice: new FormControl(null),
        sameAddress: new FormControl(null),
        facilityAddress: new FormGroup<AccountAddressFormModel>({
          city: new FormControl(null),
          country: new FormControl(null),
          line1: new FormControl(null),
          line2: new FormControl(null),
          postcode: new FormControl(null),
          county: new FormControl(null),
        }),
      }),
    );

    expect(facilityService.generateFacilityId).toHaveBeenCalled();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['../', facilityId, 'contact-details'], expect.any(Object));
  });
});
