import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { render } from '@testing-library/angular';

import { mockRequestTaskState } from '../../../testing/mock-data';
import { AUthorizationAdditionalEvidenceDecisionComponent } from './authorization-additional-evidence-decision.component';

describe('AUthorizationAdditionalEvidenceDecisionComponent', () => {
  let store: RequestTaskStore;
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let tree: Element;
  beforeEach(async () => {
    const renderResult = await render(AUthorizationAdditionalEvidenceDecisionComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setState(mockRequestTaskState);
      },
    });
    tree = renderResult.container;
  });
  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});
