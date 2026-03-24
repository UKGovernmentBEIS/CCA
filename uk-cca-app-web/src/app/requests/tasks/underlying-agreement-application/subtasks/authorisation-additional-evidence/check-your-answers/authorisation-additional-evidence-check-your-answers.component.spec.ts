import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  requestTaskQuery,
  RequestTaskStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { ActivatedRouteStub, MockType } from '@netz/common/testing';
import { TasksApiService, underlyingAgreementQuery } from '@requests/common';
import { getByRole, getByText } from '@testing';

import AuthorisationAdditionalEvidenceCheckYourAnswersComponent from './authorisation-additional-evidence-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: AuthorisationAdditionalEvidenceCheckYourAnswersComponent;
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [AuthorisationAdditionalEvidenceCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply for an underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    // Initialize state for the component to work with
    jest.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence) {
        return signal({
          authorisationAttachmentIds: ['auth1'],
          additionalEvidenceAttachmentIds: ['evidence1'],
        });
      }

      if (selector === underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments) {
        return signal({
          auth1: { fileName: 'auth1.pdf' },
          evidence1: { fileName: 'evidence1.pdf' },
        });
      }

      if (selector === requestTaskQuery.selectIsEditable) return signal(true);
      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(123);

      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal({
          underlyingAgreement: {
            authorisationAndAdditionalEvidence: {
              authorisationAttachmentIds: ['auth1'],
              additionalEvidenceAttachmentIds: ['evidence1'],
            },
          },
        });
      }

      return signal({});
    });

    fixture = TestBed.createComponent(AuthorisationAdditionalEvidenceCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = getByRole('heading', { name: 'Check your answers' }, fixture.nativeElement);
    expect(heading).toBeTruthy();
  });

  it('should render the summary sections and rows', () => {
    expect(getByText('Authorisation', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Additional evidence', fixture.nativeElement)).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Apply for an underlying agreement', fixture.nativeElement)).toBeTruthy();
  });
});
