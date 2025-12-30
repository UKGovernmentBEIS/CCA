import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockReasonForAdminTerminationPayload } from '../../../testing/mock-data';
import ReasonForAdminTerminationComponent from './reason-for-admin-termination.component';

describe('ReasonForAdminTerminationComponent', () => {
  let component: ReasonForAdminTerminationComponent;
  let fixture: ComponentFixture<ReasonForAdminTerminationComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonForAdminTerminationComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_SUBMIT' } });
    store.setPayload(mockReasonForAdminTerminationPayload);

    fixture = TestBed.createComponent(ReasonForAdminTerminationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Admin termination')).toBeInTheDocument();
    expect(screen.getByText('Provide termination reason details')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Continue')).toBeInTheDocument();
    expect(screen.getByText('Return to: Admin termination')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByText('Select reason for termination')).toBeInTheDocument();
    expect(screen.getByText('Explain why you are terminating the agreement')).toBeInTheDocument();
    expect(screen.getByText('Upload relevant files (optional)')).toBeInTheDocument();
    expect(screen.getByText('Upload relevant documents to support your explanation.')).toBeInTheDocument();
  });
});
