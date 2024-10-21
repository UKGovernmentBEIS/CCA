import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { mockAdminTerminationFinalDecisionPayload } from '../../../mocks/mock-admin-termination-final-decision-payload';
import FinalDecisionReasonActionsComponent from './final-decision-reason-actions.component';

describe('FinalDecisionReasonActionsComponent', () => {
  let component: FinalDecisionReasonActionsComponent;
  let fixture: ComponentFixture<FinalDecisionReasonActionsComponent>;
  let store: RequestTaskStore;

  const adminTerminationFinalDecisionTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const saveSubtaskSpy = jest.spyOn(adminTerminationFinalDecisionTaskService, 'saveSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinalDecisionReasonActionsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: adminTerminationFinalDecisionTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination final decision' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_FINAL_DECISION' as any } });
    store.setPayload(mockAdminTerminationFinalDecisionPayload);

    fixture = TestBed.createComponent(FinalDecisionReasonActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and content', () => {
    expect(screen.getByText('Admin termination final decision')).toBeInTheDocument();
    expect(screen.getByText('Available actions')).toBeInTheDocument();
    expect(screen.getByText('You must select the admin termination final decision.')).toBeInTheDocument();
  });

  it('should contain "Terminate agreement" and "Withdraw termination" buttons and "return to" link', () => {
    expect(screen.getByText('Terminate agreement')).toBeInTheDocument();
    expect(screen.getByText('Withdraw termination')).toBeInTheDocument();
    expect(screen.getByText('Return to: Admin termination final decision')).toBeInTheDocument();
  });

  it('should call "saveSubtask" method when the appropriate button is clicked', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Terminate agreement'));
    expect(saveSubtaskSpy).toHaveBeenCalledTimes(1);
  });
});
