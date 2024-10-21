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
import FinalDecisionReasonCheckYourAnswersComponent from './final-decision-reason-check-your-answers.component';

describe('FinalDecisionReasonCheckYourAnswersComponent', () => {
  let component: FinalDecisionReasonCheckYourAnswersComponent;
  let fixture: ComponentFixture<FinalDecisionReasonCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const adminTerminationFinalDecisionTaskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  const submitSubtaskSpy = jest.spyOn(adminTerminationFinalDecisionTaskService, 'submitSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinalDecisionReasonCheckYourAnswersComponent],
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

    fixture = TestBed.createComponent(FinalDecisionReasonCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Check your answers')).toBeInTheDocument;
    expect(screen.getByTestId('heading').innerHTML).toContain('Terminate agreement');
  });

  it('should display the correct data', () => {
    const summaryValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(summaryValues).toEqual([
      [['Decision'], ['Terminate agreement']],
      [
        ['Explain reason', 'Uploaded files'],
        ['blah blah', 'No files provided'],
      ],
    ]);
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Admin termination final decision')).toBeInTheDocument();
  });

  it('should submit form and call "submitSubtaskSpy" method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and complete'));
    expect(submitSubtaskSpy).toHaveBeenCalledTimes(1);
  });
});
