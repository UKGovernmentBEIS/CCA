import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { Mocked } from 'vitest';

import { mockVariationReviewRequestTaskState } from '../../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { FacilityDecisionComponent } from './facility-decision.component';

describe('FacilityDecisionComponent', () => {
  let fixture: ComponentFixture<FacilityDecisionComponent>;
  let store: RequestTaskStore;

  const unaTaskService: Partial<Mocked<TaskService>> = {
    saveSubtask: vi.fn().mockReturnValue(of({})),
  };

  const route = new ActivatedRouteStub({ facilityId: 'ADS_1-F00001' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityDecisionComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockVariationReviewRequestTaskState);

    fixture = TestBed.createComponent(FacilityDecisionComponent);
    fixture.detectChanges();
  });

  it('should match snapshot', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
