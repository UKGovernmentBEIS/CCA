import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { mockNonComplianceDetailsState } from '../testing/mock-data';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = {
    snapshot: {
      params: {},
      paramMap: { get: jest.fn() },
      pathFromRoot: [{ url: [{ path: 'request-task' }] }],
    },
  };

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockNonComplianceDetailsState);

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    mockTasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display snapshot', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should submit with COMPLETED status and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);

    const dto = mockTasksApiService.saveRequestTaskAction.mock.calls[0][0];
    expect(dto.requestTaskActionPayload.sectionsCompleted['provide-details']).toBe('COMPLETED');
    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: route as any });
  });
});
