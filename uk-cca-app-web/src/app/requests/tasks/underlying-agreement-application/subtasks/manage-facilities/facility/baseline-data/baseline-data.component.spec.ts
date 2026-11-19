import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { DateInputComponent } from '@netz/govuk-components';
import { FACILITY_BASELINE_DATA_FORM, FacilityBaselineDataFormModel, TasksApiService } from '@requests/common';
import { RequestTaskFileService } from '@shared/services';
import { SchemeVersionEnum } from '@shared/types';

import { UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { facilities, mockUnaRequestTaskPayload } from '../../../../../../common/underlying-agreement/testing/mock-data';
import { BaselineDataComponent } from './baseline-data.component';

describe('BaselineDataComponent date validation', () => {
  let fixture: ComponentFixture<BaselineDataComponent>;
  let form: FacilityBaselineDataFormModel;
  const saveRequestTaskAction = vi.fn();

  beforeEach(async () => {
    const payload: UnderlyingAgreementSubmitRequestTaskPayload = {
      ...mockUnaRequestTaskPayload,
      sectionsCompleted: {},
      underlyingAgreement: {
        ...mockUnaRequestTaskPayload.underlyingAgreement,
        facilities: [
          {
            ...facilities[0],
            facilityDetails: {
              ...facilities[0].facilityDetails,
              participatingSchemeVersions: [SchemeVersionEnum.CCA_3],
            },
            cca3BaselineAndTargets: {
              targetComposition: null,
              facilityBaselineEnergyConsumption: null,
              facilityTargets: null,
              baselineData: {
                isTwelveMonths: null,
                baselineDate: null,
                explanation: 'The nearest available baseline year',
                usedReportingMechanism: false,
                energyCarbonFactor: '0.5',
              },
            },
          },
        ],
      },
    };
    saveRequestTaskAction.mockReset().mockReturnValue(of(payload));

    await TestBed.configureTestingModule({
      imports: [BaselineDataComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        RequestTaskStore,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { taskId: 123, facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } },
        },
        { provide: TasksApiService, useValue: { saveRequestTaskAction } },
        {
          provide: RequestTaskFileService,
          useValue: { buildFormControl: () => new FormControl([]) },
        },
      ],
    }).compileComponents();

    const store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { id: 123, type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT', payload },
    });
    store.setIsEditable(true);
    vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(BaselineDataComponent);
    form = fixture.debugElement.injector.get(FACILITY_BASELINE_DATA_FORM);
    fixture.detectChanges();
  });

  it('keeps impossible dates invalid across Yes/No/Yes and saves only the corrected date', () => {
    const chooseTwelveMonths = (index: number) => {
      const options = fixture.nativeElement.querySelectorAll('[formControlName="isTwelveMonths"] input');
      options[index].click();
      fixture.detectChanges();
    };
    const enterDate = (day: string, month: string, year: string) => {
      const inputs = fixture.debugElement.queryAll(By.css('[govuk-date-input] input'));
      [day, month, year].forEach((value, index) => {
        inputs[index].nativeElement.value = value;
        inputs[index].nativeElement.dispatchEvent(new Event('input', { bubbles: true }));
      });
      fixture.detectChanges();
    };
    const submit = () => {
      fixture.nativeElement
        .querySelector('form')
        .dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
      fixture.detectChanges();
    };

    expect(fixture.debugElement.query(By.directive(DateInputComponent))).toBeNull();
    chooseTwelveMonths(0);
    const dateInput = fixture.debugElement.query(By.directive(DateInputComponent)).componentInstance;
    enterDate('31', '2', '2025');
    expect(form.controls.baselineDate.hasError('isUnrealDate')).toBe(true);
    expect(form.controls.baselineDate.value).toBeNull();

    // Both radio answers are booleans, so this actual flow keeps the date input mounted.
    chooseTwelveMonths(1);
    chooseTwelveMonths(0);
    expect(fixture.debugElement.query(By.directive(DateInputComponent)).componentInstance).toBe(dateInput);
    expect(form.controls.baselineDate.hasError('isUnrealDate')).toBe(true);
    expect(form.controls.baselineDate.value).toBeNull();
    submit();
    expect(saveRequestTaskAction).not.toHaveBeenCalled();

    enterDate('28', '2', '2025');
    submit();
    expect(saveRequestTaskAction).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        requestTaskActionPayload: expect.objectContaining({
          underlyingAgreement: expect.objectContaining({
            facilities: [
              expect.objectContaining({
                cca3BaselineAndTargets: expect.objectContaining({
                  baselineData: expect.objectContaining({ baselineDate: '2025-02-28' }),
                }),
              }),
            ],
          }),
        }),
      }),
    );
  });
});
