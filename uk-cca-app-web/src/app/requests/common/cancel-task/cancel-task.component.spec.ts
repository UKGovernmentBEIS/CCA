import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { TasksService } from 'cca-api';

import { mockNonComplianceDetailsState } from '../../tasks/non-compliance-details/testing/mock-data';
import { CancelTaskComponent } from './cancel-task.component';

describe('CancelTaskComponent', () => {
  let component: CancelTaskComponent;
  let fixture: ComponentFixture<CancelTaskComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub();
  const tasksService: Partial<jest.Mocked<TasksService>> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelTaskComponent],
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockNonComplianceDetailsState);

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(CancelTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksService.processRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the non-compliance cancel warning and hint', () => {
    expect(fixture.nativeElement.textContent).toContain('You will not be able to undo this action.');
    expect(fixture.nativeElement.textContent).toContain('Your task and all its data will be deleted permanently.');
  });

  it('should cancel with NON_COMPLIANCE_CANCEL_APPLICATION', () => {
    const cancelButton = Array.from(fixture.nativeElement.querySelectorAll('button')).find(
      (button: HTMLButtonElement) => button.textContent?.trim() === 'Yes, cancel this task',
    ) as HTMLButtonElement;

    cancelButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 100,
      requestTaskActionType: 'NON_COMPLIANCE_CANCEL_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['confirmation'], { relativeTo: route });
  });
});
