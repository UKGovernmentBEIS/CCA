import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockAdminTerminationFinalDecisionPayload } from '../../../mocks/mock-admin-termination-final-decision-payload';
import FinalDecisionReasonSummaryComponent from './final-decision-reason-summary.component';

describe('FinalDecisionReasonSummaryComponent', () => {
  let component: FinalDecisionReasonSummaryComponent;
  let fixture: ComponentFixture<FinalDecisionReasonSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinalDecisionReasonSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination final decision' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_FINAL_DECISION' as any } });
    store.setPayload(mockAdminTerminationFinalDecisionPayload);

    fixture = TestBed.createComponent(FinalDecisionReasonSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Summary')).toBeInTheDocument;
    expect(screen.getByTestId('heading').innerHTML).toContain('Terminate agreement');
  });

  it('should display the correct data', () => {
    const summaryValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(summaryValues).toEqual([
      [['Decision'], ['Terminate agreement']],
      [
        ['Explain reason', 'Uploaded files'],
        ['blah blah', 'No files provided'],
      ],
    ]);
  });

  it('should contain "return to" link', () => {
    expect(screen.getByText('Return to: Admin termination final decision')).toBeInTheDocument();
  });
});
