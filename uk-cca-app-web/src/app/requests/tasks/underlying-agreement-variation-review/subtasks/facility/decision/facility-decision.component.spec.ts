import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { render } from '@testing-library/angular';

import { mockVariationReviewRequestTaskState } from '../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { FacilityDecisionComponent } from './facility-decision.component';

describe('FacilityDecisionComponent', () => {
  let store: RequestTaskStore;
  let tree: Element;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const route: any = {
    snapshot: {
      params: {
        facilityId: 'ADS_1-F00001',
      },
      pathFromRoot: [],
    },
  };

  beforeEach(async () => {
    const renderResult = await render(FacilityDecisionComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setState(mockVariationReviewRequestTaskState);
      },
    });

    tree = renderResult.container;
  });

  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});
