import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { mockNonComplianceDetailsState } from '../testing/mock-data';
import { NonComplianceCompleteTaskComponent } from './complete-task.component';

describe('NonComplianceCompleteTaskComponent', () => {
  let component: NonComplianceCompleteTaskComponent;
  let fixture: ComponentFixture<NonComplianceCompleteTaskComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = {
    snapshot: {
      params: {},
      paramMap: { get: vi.fn() },
      pathFromRoot: [],
    },
  };

  const mockTasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceCompleteTaskComponent],
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

    fixture = TestBed.createComponent(NonComplianceCompleteTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    mockTasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit with NON_COMPLIANCE_DETAILS_SUBMIT_APPLICATION and EMPTY_PAYLOAD', () => {
    component.onComplete();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 100,
      requestTaskActionType: 'NON_COMPLIANCE_DETAILS_SUBMIT_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });

  it('should navigate to confirmation with replaceUrl true after submit', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');

    component.onComplete();

    expect(navigateSpy).toHaveBeenCalledWith(['confirmation'], {
      relativeTo: route as any,
      replaceUrl: true,
    });
  });
});
