import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import { TasksApiService, underlyingAgreementQuery } from '@requests/common';

import { FacilityApplyRuleComponent } from './facility-apply-rule.component';

describe('FacilityApplyRuleComponent', () => {
  let fixture: ComponentFixture<FacilityApplyRuleComponent>;
  let store: RequestTaskStore;
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
          underlyingAgreementAttachments: {
            'evidence-file-uuid': 'evidenceFile.xlsx',
          },
        }),
      ),
    };

    TestBed.configureTestingModule({
      imports: [FacilityApplyRuleComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
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
                facilityDetails: { name: 'Test Facility' },
                apply70Rule: {
                  energyConsumed: 50,
                  energyConsumedEligible: 70,
                  energyConsumedProvision: 40,
                  evidenceFile: 'evidence-file-uuid',
                },
              },
            ],
          },
          underlyingAgreementAttachments: {
            'evidence-file-uuid': 'evidenceFile.xlsx',
          },
        });
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(123);
      if (selector === requestTaskQuery.selectRequestTaskType) return signal('UNDERLYING_AGREEMENT_SUBMIT_APPLICATION');
      if (selector === underlyingAgreementQuery.selectAttachments) {
        return signal({
          'evidence-file-uuid': 'evidenceFile.xlsx',
        });
      }

      if (selector === underlyingAgreementQuery.selectFacility('ADS_1-F00001')) {
        return signal({
          facilityId: 'ADS_1-F00001',
          facilityDetails: { name: 'Test Facility' },
          apply70Rule: {
            energyConsumed: 50,
            energyConsumedEligible: 70,
            threeSeventhsProvision: 40,
            evidenceFile: 'evidence-file-uuid',
          },
        });
      }

      return signal({});
    });

    fixture = TestBed.createComponent(FacilityApplyRuleComponent);
    fixture.detectChanges();
  });

  it('should show form values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
