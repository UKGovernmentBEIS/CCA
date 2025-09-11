import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { mockRequestTaskItemDTO } from '../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import VariationDetailsCheckYourAnswersComponent from './variation-details-check-your-answers.component';

describe('VariationDetailsCheckYourAnswersComponent', () => {
  let component: VariationDetailsCheckYourAnswersComponent;
  let fixture: ComponentFixture<VariationDetailsCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const mockTasksApiService: Partial<jest.Mocked<TasksApiService>> = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<VariationDetailsCheckYourAnswersComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('dt')) ?? []),
          ...(Array.from(row.querySelectorAll('dd')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VariationDetailsCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(VariationDetailsCheckYourAnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Check your answers');

    expect(page.summaryListValues).toEqual([
      ['Target Unit/Facility changes', 'Amend the 70% rule evaluation for one or more facilities'],
      ['Amend the target currency to', 'change the throughput unit'],
      ['Explain what you are changing and the reason for the changes', 'Variation reason'],
      ['Decision status', 'Accepted'],
      ['Notes', 'Variation details notes'],
      ['Uploaded files', 'No files provided'],
    ]);
  });

  it('should submit', () => {
    const apiServiceSpy = jest.spyOn(mockTasksApiService, 'saveRequestTaskAction');

    page.submitButton.click();
    fixture.detectChanges();

    expect(apiServiceSpy).toHaveBeenCalledTimes(1);
    expect(apiServiceSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW',
      }),
    );
  });
});
