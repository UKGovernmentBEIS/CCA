import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockAdminTerminationFinalDecisionPayload } from '../../../testing/mock-data';
import FinalDecisionReasonActionsComponent from './final-decision-reason-actions.component';

describe('FinalDecisionReasonActionsComponent', () => {
  let component: FinalDecisionReasonActionsComponent;
  let fixture: ComponentFixture<FinalDecisionReasonActionsComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinalDecisionReasonActionsComponent],
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
});
