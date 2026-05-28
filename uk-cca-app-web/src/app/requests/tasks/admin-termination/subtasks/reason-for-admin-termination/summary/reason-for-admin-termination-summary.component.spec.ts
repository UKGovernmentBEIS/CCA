import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockReasonForAdminTerminationPayload } from '../../../testing/mock-data';
import ReasonForAdminTerminationSummaryComponent from './reason-for-admin-termination-summary.component';

describe('ReasonForAdminTerminationSummaryComponent', () => {
  let component: ReasonForAdminTerminationSummaryComponent;
  let fixture: ComponentFixture<ReasonForAdminTerminationSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonForAdminTerminationSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' } });
    store.setPayload(mockReasonForAdminTerminationPayload);

    fixture = TestBed.createComponent(ReasonForAdminTerminationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper summary values', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
