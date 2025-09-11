import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockUNAReviewRequestTaskState, TasksApiService } from '@requests/common';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import AuthorisationAdditionalEvidenceCheckYourAnswersComponent from './authorisation-additional-evidence-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: AuthorisationAdditionalEvidenceCheckYourAnswersComponent;
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub();

  const tasksApiService: Partial<jest.Mocked<TasksApiService>> = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const saveRequestTaskActionSpy = jest.spyOn(tasksApiService, 'saveRequestTaskAction');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorisationAdditionalEvidenceCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);

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
    expect(screen.getByText('Authorisation')).toBeInTheDocument();
    expect(screen.getByText('Additional evidence')).toBeInTheDocument();
    expect(screen.getAllByText('No files provided')).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Review application for underlying agreement')).toBeInTheDocument();
  });

  it('should submit form and call saveRequestTaskAction method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and complete'));
    expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);
  });
});
