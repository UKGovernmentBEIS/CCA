import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { mockRequestTaskItemDTO } from '../../../../testing/mock-data';
import FacilityCheckAnswersComponent from './facility-check-answers.component';

describe('FacilityCheckAnswersComponent', () => {
  let component: FacilityCheckAnswersComponent;
  let fixture: ComponentFixture<FacilityCheckAnswersComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = {
    snapshot: {
      params: {
        facilityId: 'ADS_1-F00001',
      },
      paramMap: {
        get: jest.fn().mockReturnValue(mockRequestTaskItemDTO.requestTask.id),
      },
      pathFromRoot: [],
    },
  };

  const mockTasksApiService: Partial<jest.Mocked<TasksApiService>> = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityCheckAnswersComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get sections() {
      return this.queryAll<HTMLHeadingElement>('h2');
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
      imports: [FacilityCheckAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: mockTasksApiService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(FacilityCheckAnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Check your answers');
    expect(fixture).toMatchSnapshot();
  });

  it('should edit facility extent and save', () => {
    const apiServiceSpy = jest.spyOn(mockTasksApiService, 'saveRequestTaskAction');

    page.submitButton.click();
    fixture.detectChanges();

    expect(apiServiceSpy).toHaveBeenCalledTimes(1);
    expect(apiServiceSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION',
      }),
    );
  });
});
