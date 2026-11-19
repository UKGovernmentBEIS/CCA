import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { EMPTY } from 'rxjs';

import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  RequestTaskState,
  RequestTaskStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService, UNAVariationReviewRequestTaskPayload } from '@requests/common';
import { mockVariationReviewRequestTaskState } from '@requests/common/underlying-agreement/testing/variation-review-mock-data';
import { Draft, produce } from 'immer';

import { TargetPeriod6Details } from 'cca-api';

import { createSaveActionDTO } from '../../../transform';
import { AddBaselineDataComponent } from './add-baseline-data.component';
import { ADD_BASELINE_DATA_FORM } from './add-baseline-data-form.provider';

/**
 * Applies the same change to the current and the original TP6, so an unchanged save compares equal. The API
 * returns baseline dates as 'yyyy-MM-dd', and the stored target matches the one the side effects recalculate.
 */
function buildState(recipe: (tp6: Draft<TargetPeriod6Details>) => void = () => undefined): RequestTaskState {
  return produce(mockVariationReviewRequestTaskState, (draft) => {
    const payload = draft.requestTaskItem.requestTask.payload as UNAVariationReviewRequestTaskPayload;

    for (const tp6 of [
      payload.underlyingAgreement.targetPeriod6Details,
      payload.originalUnderlyingAgreementContainer.underlyingAgreement.targetPeriod6Details,
    ]) {
      tp6.baselineData.baselineDate = '2020-12-12';
      tp6.baselineData.greenfieldEvidences = [];
      tp6.targets.target = '9.99';
      recipe(tp6);
    }
  });
}

async function setup(state = buildState()) {
  await TestBed.configureTestingModule({
    imports: [AddBaselineDataComponent],
    providers: [
      provideHttpClient(withXhr()),
      provideHttpClientTesting(),
      { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      RequestTaskStore,
      { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
    ],
  }).compileComponents();

  TestBed.inject(RequestTaskStore).setState(state);
  const saveSpy = vi.spyOn(TestBed.inject(TasksApiService), 'saveRequestTaskAction').mockReturnValue(EMPTY);

  const fixture = TestBed.createComponent(AddBaselineDataComponent);
  const form = fixture.debugElement.injector.get(ADD_BASELINE_DATA_FORM);
  fixture.detectChanges();

  const submit = () => {
    fixture.componentInstance.onSubmit();
    return (saveSpy.mock.calls[0][0] as ReturnType<typeof createSaveActionDTO>).requestTaskActionPayload;
  };

  return { form, submit, payload: state.requestTaskItem.requestTask.payload as UNAVariationReviewRequestTaskPayload };
}

describe('AddBaselineDataComponent (variation review TP6)', () => {
  it('should save the baseline data into TP6 as strings and leave TP5 unchanged', async () => {
    const { form, submit, payload } = await setup();
    form.controls.energy.setValue('250');

    const { underlyingAgreement } = submit();

    expect(underlyingAgreement.targetPeriod6Details.baselineData).toEqual(
      expect.objectContaining({ energy: '250', throughput: '10', energyCarbonFactor: '1', performance: '25' }),
    );
    expect(underlyingAgreement.targetPeriod5Details).toEqual(payload.underlyingAgreement.targetPeriod5Details);
  });

  it('should send a cleared value as null rather than the string "null"', async () => {
    const { form, submit } = await setup();
    form.controls.energyCarbonFactor.setValue(null);

    const { underlyingAgreement } = submit();

    expect(underlyingAgreement.targetPeriod6Details.baselineData.energyCarbonFactor).toBeNull();
  });

  it('should send the performance as null when the target composition is not relative', async () => {
    const { submit } = await setup(
      buildState((tp6) => {
        tp6.targetComposition.agreementCompositionType = 'ABSOLUTE';
      }),
    );

    const { underlyingAgreement } = submit();

    expect(underlyingAgreement.targetPeriod6Details.baselineData.performance).toBeNull();
  });

  it('should keep the TP6 decision when the baseline data is saved unchanged', async () => {
    const { submit, payload } = await setup();

    const { reviewGroupDecisions } = submit();

    expect(reviewGroupDecisions.TARGET_PERIOD6_DETAILS).toEqual(payload.reviewGroupDecisions.TARGET_PERIOD6_DETAILS);
  });

  it('should remove the TP6 decision when the baseline data changes', async () => {
    const { form, submit } = await setup();
    form.controls.energy.setValue('250');

    const { reviewGroupDecisions } = submit();

    expect(reviewGroupDecisions.TARGET_PERIOD6_DETAILS).toBeUndefined();
  });
});
