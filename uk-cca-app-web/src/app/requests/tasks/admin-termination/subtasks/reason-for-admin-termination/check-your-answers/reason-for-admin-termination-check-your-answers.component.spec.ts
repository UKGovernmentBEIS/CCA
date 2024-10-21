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

import { mockReasonForAdminTerminationPayload } from '../../../mocks/mock-admin-termination-payload';
import ReasonForAdminTerminationSummaryComponent from './reason-for-admin-termination-check-your-answers.component';

describe('ReasonForAdminTerminationSummaryComponent', () => {
  let component: ReasonForAdminTerminationSummaryComponent;
  let fixture: ComponentFixture<ReasonForAdminTerminationSummaryComponent>;
  let store: RequestTaskStore;

  const adminTerminationTaskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  const submitSubtaskSpy = jest.spyOn(adminTerminationTaskService, 'submitSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonForAdminTerminationSummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: adminTerminationTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_SUBMIT' as any } });
    store.setPayload(mockReasonForAdminTerminationPayload);

    fixture = TestBed.createComponent(ReasonForAdminTerminationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Admin termination')).toBeInTheDocument();
    expect(screen.getByText('Check your answers')).toBeInTheDocument();
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
      [
        ['Termination reason', 'Explain why the account is being terminated', 'Uploaded files'],
        ['Failure to agree a variation in a target', 'mplah mplah', 'No files provided'],
      ],
    ]);
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Admin termination')).toBeInTheDocument();
  });

  it('should submit form and call "submitSubtaskSpy" method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and complete'));
    expect(submitSubtaskSpy).toHaveBeenCalledTimes(1);
  });
});
