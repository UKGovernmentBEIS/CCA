import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState } from '@requests/common';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import AuthorisationAdditionalEvidenceCheckYourAnswersComponent from './authorisation-additional-evidence-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: AuthorisationAdditionalEvidenceCheckYourAnswersComponent;
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub();
  const taskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };
  const submitSubtaskSpy = jest.spyOn(taskService, 'submitSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorisationAdditionalEvidenceCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TaskService, useValue: taskService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply for an underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(AuthorisationAdditionalEvidenceCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = screen.getByRole('heading', { name: 'Check your answers' });
    expect(heading).toBeInTheDocument();
  });

  it('should render the summary sections and rows', () => {
    expect(screen.getByText('Authorisation and additional evidence')).toBeInTheDocument();
    const authorisationAndAdditionalEvidenceHeading = screen.getAllByText('Uploaded files');
    expect(
      authorisationAndAdditionalEvidenceHeading[0] && authorisationAndAdditionalEvidenceHeading[1],
    ).toHaveTextContent('Uploaded files');
    expect(screen.getByText('No files provided')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Apply for an underlying agreement')).toBeInTheDocument();
  });

  it('should submit form and call submitSubtask method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and complete'));
    expect(submitSubtaskSpy).toHaveBeenCalledTimes(1);
  });
});
