import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
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
      paramMap: { get: vi.fn() },
      pathFromRoot: [{ url: [{ path: 'request-task' }] }],
    },
  };

  const mockTasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
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
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });

  it('should hide confirm and complete button when task is not editable', () => {
    store.setState({ ...mockNonComplianceDetailsState, isEditable: false });

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button');
    expect(button).toBeFalsy();
  });

  it('should not submit when task is not editable', () => {
    store.setState({ ...mockNonComplianceDetailsState, isEditable: false });

    fixture.detectChanges();
    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
  });

  it('should submit with COMPLETED status and navigate back to the task list', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');

    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);

    const dto = mockTasksApiService.saveRequestTaskAction.mock.calls[0][0];
    expect(dto.requestTaskActionPayload.sectionsCompleted['provide-details']).toBe('COMPLETED');
    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: route as any });
  });
});
