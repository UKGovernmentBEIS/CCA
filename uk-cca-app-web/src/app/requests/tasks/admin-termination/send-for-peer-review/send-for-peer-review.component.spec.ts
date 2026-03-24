import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { getByText } from '@testing';

import { TasksService } from 'cca-api';

import AdminTerminationSendForPeerReviewComponent from './send-for-peer-review.component';

describe('AdminTerminationSendForPeerReviewComponent', () => {
  let component: AdminTerminationSendForPeerReviewComponent;
  let fixture: ComponentFixture<AdminTerminationSendForPeerReviewComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const mockCandidateAssignees = [
    { id: 'user1', firstName: 'John', lastName: 'Smith' },
    { id: 'user2', firstName: 'Sarah', lastName: 'Johnson' },
    { id: 'user3', firstName: 'Mike', lastName: 'Brown' },
  ];

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const activatedRoute = {
    snapshot: {
      paramMap: convertToParamMap({ taskId: 1 }),
      params: { taskId: '123' },
      data: {
        candidateAssignees: mockCandidateAssignees,
      },
      pathFromRoot: [
        { url: [{ path: 'tasks' }] },
        { url: [{ path: 'admin-termination' }] },
        { url: [{ path: 'send-for-peer-review' }] },
      ],
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationSendForPeerReviewComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    store.setRequestTaskItem({
      requestTask: { id: 456, type: 'ADMIN_TERMINATION_APPLICATION_SUBMIT' },
      requestInfo: { accountId: 1 },
    });

    fixture = TestBed.createComponent(AdminTerminationSendForPeerReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('Send for peer review')).toBeTruthy();
    expect(getByText('Select peer reviewer')).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Confirm and complete')).toBeTruthy();
    expect(getByText('Return to: Admin Termination')).toBeTruthy();
  });

  it('should display the correct form field', () => {
    expect(getByText('Select a peer reviewer')).toBeTruthy();
  });

  it('should populate select options with candidate assignees', () => {
    const selectElement = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    expect(selectElement).toBeTruthy();

    // Check that options are populated with formatted names
    expect(getByText('John Smith')).toBeTruthy();
    expect(getByText('Sarah Johnson')).toBeTruthy();
    expect(getByText('Mike Brown')).toBeTruthy();
  });

  it('should submit form and call processRequestTaskAction method', async () => {
    const selectElement = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    selectElement.value = '0: user1';
    selectElement.dispatchEvent(new Event('change', { bubbles: true }));
    fixture.detectChanges();

    const submitButton = Array.from(fixture.nativeElement.querySelectorAll('button')).find(
      (button: HTMLButtonElement) => button.textContent?.trim() === 'Confirm and complete',
    ) as HTMLButtonElement;
    submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ADMIN_TERMINATION_REQUEST_PEER_REVIEW',
      requestTaskActionPayload: {
        payloadType: 'ADMIN_TERMINATION_PEER_REVIEW_REQUEST_PAYLOAD',
        peerReviewer: 'user1',
      },
      requestTaskId: 123,
    });

    expect(router.navigate).toHaveBeenCalledWith(['confirmation', 'user1'], {
      relativeTo: activatedRoute,
    });
  });
});
