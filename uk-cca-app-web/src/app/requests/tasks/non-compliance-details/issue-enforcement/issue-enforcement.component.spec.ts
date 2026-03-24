import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { mockNonComplianceDetailsState } from '../testing/mock-data';
import { IssueEnforcementComponent } from './issue-enforcement.component';

describe('IssueEnforcementComponent', () => {
  let component: IssueEnforcementComponent;
  let fixture: ComponentFixture<IssueEnforcementComponent>;
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
      imports: [IssueEnforcementComponent],
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

    fixture = TestBed.createComponent(IssueEnforcementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    mockTasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and call saveRequestTaskAction', () => {
    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
  });

  it('should navigate after submit to check your answers', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route as any });
  });

  it('should submit with isEnforcementResponseNoticeRequired and conditional explanation', () => {
    const form = (component as any).form;

    form.patchValue({
      isEnforcementResponseNoticeRequired: false,
      explanation: 'Some reason',
    });

    component.onSubmit();

    let dto = mockTasksApiService.saveRequestTaskAction.mock.calls.at(-1)?.[0];
    expect(dto.requestTaskActionPayload.nonComplianceDetails.isEnforcementResponseNoticeRequired).toBe(false);
    expect(dto.requestTaskActionPayload.nonComplianceDetails.explanation).toBe('Some reason');

    form.patchValue({
      isEnforcementResponseNoticeRequired: true,
      explanation: 'Ignored explanation',
    });

    component.onSubmit();

    dto = mockTasksApiService.saveRequestTaskAction.mock.calls.at(-1)?.[0];
    expect(dto.requestTaskActionPayload.nonComplianceDetails.isEnforcementResponseNoticeRequired).toBe(true);
    expect(dto.requestTaskActionPayload.nonComplianceDetails.explanation).toBeNull();
  });
});
