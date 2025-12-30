import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockReasonForAdminTerminationPayload } from '../../../testing/mock-data';
import ReasonForAdminTerminationSummaryComponent from './reason-for-admin-termination-check-your-answers.component';

describe('ReasonForAdminTerminationSummaryComponent', () => {
  let component: ReasonForAdminTerminationSummaryComponent;
  let fixture: ComponentFixture<ReasonForAdminTerminationSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonForAdminTerminationSummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_SUBMIT' } });
    store.setPayload(mockReasonForAdminTerminationPayload);

    fixture = TestBed.createComponent(ReasonForAdminTerminationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
