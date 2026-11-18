import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { mockAdminTerminationFinalDecisionPayload } from '../../../testing/mock-data';
import FinalDecisionReasonDetailsComponent from './final-decision-reason-details.component';

describe('FinalDecisionReasonDetailsComponent', () => {
  let component: FinalDecisionReasonDetailsComponent;
  let fixture: ComponentFixture<FinalDecisionReasonDetailsComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinalDecisionReasonDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination final decision' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_FINAL_DECISION' } });
    store.setPayload(mockAdminTerminationFinalDecisionPayload);

    fixture = TestBed.createComponent(FinalDecisionReasonDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('Explain the reason for your decision', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Terminate agreement', fixture.nativeElement)).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Continue', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Admin termination final decision', fixture.nativeElement)).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByText('Explain the reason for your decision', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload relevant files (optional)', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload relevant documents to support your explanation.', fixture.nativeElement)).toBeTruthy();
  });
});
