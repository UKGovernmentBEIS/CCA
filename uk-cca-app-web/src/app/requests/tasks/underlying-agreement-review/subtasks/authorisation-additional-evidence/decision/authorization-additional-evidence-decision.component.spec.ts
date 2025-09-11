import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { mockUNAReviewRequestTaskState } from '@requests/common';
import { render } from '@testing-library/angular';

import { AuthorizationAdditionalEvidenceDecisionComponent } from './authorization-additional-evidence-decision.component';

describe('TP5 Decision', () => {
  let store: RequestTaskStore;
  let tree: Element;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    const renderResult = await render(AuthorizationAdditionalEvidenceDecisionComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setState(mockUNAReviewRequestTaskState);
      },
    });

    tree = renderResult.container;
  });

  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});
