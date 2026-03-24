import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText, queryByText } from '@testing';
import { produce } from 'immer';

import { mockVariationReviewRequestTaskState } from '../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { AdditionalInfoComponent } from './additional-info.component';

describe('AdditionalInfoComponent', () => {
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  let store: RequestTaskStore;
  let fixture: ComponentFixture<AdditionalInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdditionalInfoComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockVariationReviewRequestTaskState);
    fixture = TestBed.createComponent(AdditionalInfoComponent);
    fixture.detectChanges();
  });

  describe('when determination type is REJECTED', () => {
    it('should match snapshot', () => {
      expect(fixture.nativeElement).toMatchSnapshot();
    });

    it('should not show variation impacts agreement question', async () => {
      const questionText = 'Does this variation result in changes to the current underlying agreement?';
      expect(queryByText(questionText)).toBeFalsy();
    });

    it('should display Reject caption', async () => {
      expect(getByText('Reject')).toBeTruthy();
    });

    it('should display common elements', async () => {
      expect(getByText(/Additional information/)).toBeTruthy();
      expect(getByText('Continue')).toBeTruthy();
      expect(getByText('Provide any additional information here to support your decision')).toBeTruthy();
    });
  });

  describe('when determination type is ACCEPTED', () => {
    beforeEach(() => {
      const acceptedState = produce(mockVariationReviewRequestTaskState, (draft) => {
        const payload = draft.requestTaskItem.requestTask.payload as any;
        payload.determination.type = 'ACCEPTED';
      });
      store.setState(acceptedState);
      fixture.detectChanges();
    });

    it('should match snapshot', () => {
      expect(fixture.nativeElement).toMatchSnapshot();
    });

    it('should show variation impacts agreement question', async () => {
      const questionText = 'Does this variation result in changes to the current underlying agreement?';
      expect(getByText(questionText)).toBeTruthy();
    });

    it('should display Yes and No radio options with correct hints', async () => {
      expect(getByText('Yes')).toBeTruthy();
      expect(getByText('No')).toBeTruthy();
      expect(getByText(/A new version of the underlying agreement will be generated/)).toBeTruthy();
      expect(getByText(/A notification letter will be generated to acknowledge/)).toBeTruthy();
    });

    it('should display Accept caption', async () => {
      expect(getByText('Accept')).toBeTruthy();
    });

    it('should display common elements', async () => {
      expect(getByText(/Additional information/)).toBeTruthy();
      expect(getByText('Continue')).toBeTruthy();
      expect(getByText('Provide any additional information here to support your decision')).toBeTruthy();
    });
  });

  describe('when determination type is ACCEPTED and variationImpactsAgreement is false', () => {
    beforeEach(() => {
      const acceptedNoChangesState = produce(mockVariationReviewRequestTaskState, (draft) => {
        const payload = draft.requestTaskItem.requestTask.payload as any;
        payload.determination.type = 'ACCEPTED';
        payload.determination.variationImpactsAgreement = false;
      });
      store.setState(acceptedNoChangesState);
      fixture.detectChanges();
    });

    it('should match snapshot', () => {
      expect(fixture.nativeElement).toMatchSnapshot();
    });

    it('should not show description examples details', async () => {
      expect(queryByText('Description examples')).toBeFalsy();
    });

    it('should not show the responsible person bullet point', async () => {
      expect(queryByText(/We have updated the personal information of the Responsible Person/)).toBeFalsy();
    });
  });
});
