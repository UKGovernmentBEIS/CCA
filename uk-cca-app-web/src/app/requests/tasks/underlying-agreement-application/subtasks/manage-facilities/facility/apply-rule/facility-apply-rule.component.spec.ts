import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { NEVER, of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, MockType } from '@netz/common/testing';
import { TasksApiService, underlyingAgreementQuery } from '@requests/common';

import { UnderlyingAgreementSaveRequestTaskActionPayload } from 'cca-api';

import { FacilityApplyRuleComponent } from './facility-apply-rule.component';

describe('FacilityApplyRuleComponent', () => {
  let fixture: ComponentFixture<FacilityApplyRuleComponent>;
  let store: RequestTaskStore;
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
          underlyingAgreementAttachments: {
            'evidence-file-uuid': 'evidenceFile.xlsx',
          },
        }),
      ),
    };

    TestBed.configureTestingModule({
      imports: [FacilityApplyRuleComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    // Mock store select methods
    vi.spyOn(store, 'select').mockImplementation((selector) => {
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
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });

  // The form disables and resets the 3/7ths provision and its start date once the energy consumed
  // reaches 70%, so `form.value` used to leave the previously saved values in the payload.
  it('should save the cleared 3/7ths provision when the energy consumed reaches 70%', () => {
    // Untyped: the form holds numbers and dates, while the api model types these fields as strings.
    const form: FormGroup = fixture.componentInstance['form'];
    form.patchValue({
      energyConsumed: 50,
      energyConsumedProvision: 40,
      startDate: new Date('2020-01-01'),
    });
    fixture.detectChanges();

    expect(form.get('energyConsumedProvision').enabled).toBe(true);

    form.get('energyConsumed').setValue(80);
    fixture.detectChanges();

    expect(form.get('energyConsumedProvision').disabled).toBe(true);
    expect(form.getRawValue().energyConsumedProvision).toBeNull();

    // The submit callback navigates once the task is saved; the test only cares about the payload.
    tasksApiService.saveRequestTaskAction.mockReturnValue(NEVER);
    fixture.componentInstance.onSubmit();

    const dto = tasksApiService.saveRequestTaskAction.mock.calls[0][0];
    const apply70Rule = (dto.requestTaskActionPayload as UnderlyingAgreementSaveRequestTaskActionPayload)
      .underlyingAgreement.facilities[0].apply70Rule;

    expect(apply70Rule.energyConsumed).toBe(80);
    expect(apply70Rule.energyConsumedProvision).toBeNull();
    expect(apply70Rule.startDate).toBeNull();
  });
});
