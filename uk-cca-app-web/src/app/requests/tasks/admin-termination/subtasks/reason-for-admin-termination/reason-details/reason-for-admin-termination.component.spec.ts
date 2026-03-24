import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

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
    expect(getByText('Admin termination', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Provide termination reason details', fixture.nativeElement)).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Continue', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Admin termination', fixture.nativeElement)).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByText('Select reason for termination', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Explain why you are terminating the agreement', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload relevant files (optional)', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload relevant documents to support your explanation.', fixture.nativeElement)).toBeTruthy();
  });
});
