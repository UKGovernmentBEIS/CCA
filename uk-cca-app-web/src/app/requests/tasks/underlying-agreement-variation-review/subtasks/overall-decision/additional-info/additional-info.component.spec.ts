import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { render, screen } from '@testing-library/angular';
import { produce } from 'immer';

import { mockVariationReviewRequestTaskState } from '../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { AdditionalInfoComponent } from './additional-info.component';

describe('AdditionalInfoComponent', () => {
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  describe('when determination type is REJECTED', () => {
    let store: RequestTaskStore;
    let container: Element;

    beforeEach(async () => {
      const result = await render(AdditionalInfoComponent, {
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
          store.setState(mockVariationReviewRequestTaskState);
        },
      });

      container = result.container;
    });

    it('should match snapshot', async () => {
      expect(container).toMatchSnapshot();
    });

    it('should not show variation impacts agreement question', async () => {
      const questionText = 'Does this variation result in changes to the current underlying agreement?';
      expect(screen.queryByText(questionText)).not.toBeInTheDocument();
    });

    it('should display Reject caption', async () => {
      expect(screen.getByText('Reject')).toBeInTheDocument();
    });

    it('should display common elements', async () => {
      expect(screen.getByText(/Add any additional information/)).toBeInTheDocument();
      expect(screen.getByText('Continue')).toBeInTheDocument();
      expect(screen.getByText('Provide any additional information here to support your decision')).toBeInTheDocument();
    });
  });

  describe('when determination type is ACCEPTED', () => {
    let store: RequestTaskStore;
    let container: Element;

    beforeEach(async () => {
      const acceptedState = produce(mockVariationReviewRequestTaskState, (draft) => {
        draft.requestTaskItem.requestTask.payload.determination.type = 'ACCEPTED';
      });

      const result = await render(AdditionalInfoComponent, {
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
          store.setState(acceptedState);
        },
      });

      container = result.container;
    });

    it('should match snapshot', async () => {
      expect(container).toMatchSnapshot();
    });

    it('should show variation impacts agreement question', async () => {
      const questionText = 'Does this variation result in changes to the current underlying agreement?';
      expect(screen.getByText(questionText)).toBeInTheDocument();
    });

    it('should display Yes and No radio options with correct hints', async () => {
      expect(screen.getByText('Yes')).toBeInTheDocument();
      expect(screen.getByText('No')).toBeInTheDocument();
      expect(screen.getByText(/A new version of the underlying agreement will be generated/)).toBeInTheDocument();
      expect(screen.getByText(/A notification letter will be generated to acknowledge/)).toBeInTheDocument();
    });

    it('should display Accept caption', async () => {
      expect(screen.getByText('Accept')).toBeInTheDocument();
    });

    it('should display common elements', async () => {
      expect(screen.getByText(/Add any additional information/)).toBeInTheDocument();
      expect(screen.getByText(/This text will be included in the official notice/)).toBeInTheDocument();
      expect(screen.getByText('Continue')).toBeInTheDocument();
      expect(screen.getByText('Provide any additional information here to support your decision')).toBeInTheDocument();
    });
  });
});
