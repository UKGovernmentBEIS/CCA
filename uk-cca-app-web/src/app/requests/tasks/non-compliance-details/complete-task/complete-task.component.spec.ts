import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
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
      paramMap: { get: jest.fn() },
      pathFromRoot: [],
    },
  };

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceCompleteTaskComponent],
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
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.onComplete();

    expect(navigateSpy).toHaveBeenCalledWith(['confirmation'], {
      relativeTo: route as any,
      replaceUrl: true,
    });
  });
});
