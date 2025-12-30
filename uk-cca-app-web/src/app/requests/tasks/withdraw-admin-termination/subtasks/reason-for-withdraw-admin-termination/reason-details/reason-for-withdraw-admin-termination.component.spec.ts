import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockReasonForAdminTerminationWithdrawPayload } from '../../../testing/mock-data';
import ReasonForWithdrawAdminTerminationComponent from './reason-for-withdraw-admin-termination.component';

describe('ReasonForWithdrawAdminTerminationComponent', () => {
  let component: ReasonForWithdrawAdminTerminationComponent;
  let fixture: ComponentFixture<ReasonForWithdrawAdminTerminationComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonForWithdrawAdminTerminationComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Withdraw admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_WITHDRAW' } });
    store.setPayload(mockReasonForAdminTerminationWithdrawPayload);

    fixture = TestBed.createComponent(ReasonForWithdrawAdminTerminationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Withdraw admin termination')).toBeInTheDocument();
    expect(screen.getByText('Reason for withdrawing the admin termination')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Continue')).toBeInTheDocument();
    expect(screen.getByText('Return to: Withdraw admin termination')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByText('Explain why you are withdrawing the admin termination')).toBeInTheDocument();
    expect(screen.getByText('Upload relevant files (optional)')).toBeInTheDocument();
    expect(screen.getByText('Upload relevant documents to support your explanation.')).toBeInTheDocument();
  });
});
