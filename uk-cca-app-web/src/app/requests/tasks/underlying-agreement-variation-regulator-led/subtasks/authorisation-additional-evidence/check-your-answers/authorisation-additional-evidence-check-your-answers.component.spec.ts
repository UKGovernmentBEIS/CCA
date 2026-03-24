import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { click, getAllByText, getByText } from '@testing';

import { mockRequestTaskState } from '../../../testing/mock-data';
import AuthorisationAdditionalEvidenceCheckYourAnswersComponent from './authorisation-additional-evidence-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: AuthorisationAdditionalEvidenceCheckYourAnswersComponent;
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub();

  const mockTasksApiService: Partial<jest.Mocked<TasksApiService>> = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const submitSubtaskSpy = jest.spyOn(mockTasksApiService, 'saveRequestTaskAction');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorisationAdditionalEvidenceCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Vary the underlying agreement' },
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
    const heading = getByText('Check your answers');
    expect(heading).toBeTruthy();
  });

  it('should render the summary sections and rows', () => {
    const authorisationAndAdditionalEvidenceHeadingAndSummaryKey = getAllByText('Authorisation');
    expect(
      authorisationAndAdditionalEvidenceHeadingAndSummaryKey[0] &&
        authorisationAndAdditionalEvidenceHeadingAndSummaryKey[1],
    ).toBeTruthy();
    const additionalEvidenceHeadingAndSummaryKey = getAllByText('Authorisation');
    expect(additionalEvidenceHeadingAndSummaryKey[0] && additionalEvidenceHeadingAndSummaryKey[1]).toBeTruthy();
    const noFilesProvided = getAllByText('No files provided');
    expect(((noFilesProvided[0] && noFilesProvided[1]) as HTMLElement | null)?.textContent ?? '').toContain(
      'No files provided',
    );
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Confirm and complete')).toBeTruthy();
    expect(getByText('Return to: Vary the underlying agreement')).toBeTruthy();
  });

  it('should submit form and call submitSubtask method', () => {
    click(getByText('Confirm and complete'));
    expect(submitSubtaskSpy).toHaveBeenCalledTimes(1);
  });
});
