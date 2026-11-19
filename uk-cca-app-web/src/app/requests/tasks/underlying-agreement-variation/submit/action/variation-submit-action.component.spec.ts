import { HttpErrorResponse, provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { ErrorCodes } from '@error/business-errors';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub, MockType } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { AnalyticsService, JourneyOutcomeEvent } from '@shared/services';
import { getByText } from '@testing';

import {
  mockRequestTaskState,
  mockUnaVariationRequestTaskPayload,
  mockUnderlyingAgreementVariation,
} from '../../testing/mock-data';
import { VariationSubmitActionComponent } from './variation-submit-action.component';

describe('VariationSubmitActionComponent', () => {
  let component: VariationSubmitActionComponent;
  let fixture: ComponentFixture<VariationSubmitActionComponent>;
  let router: Router;
  let store: RequestTaskStore;

  const activatedRoute = new ActivatedRouteStub();
  const analyticsService = { push: vi.fn() };

  const mockTasksApiService: MockType<TasksApiService> = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const submit = () => {
    fixture.nativeElement.querySelector('button.govuk-button').click();
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: AnalyticsService, useValue: analyticsService },
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);
    analyticsService.push.mockClear();

    fixture = TestBed.createComponent(VariationSubmitActionComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and text', () => {
    const heading = getByText('Send variation application to regulator');
    expect(heading).toBeTruthy();
  });

  it('should submit and navigate to confirmation page', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    const apiServiceSpy = vi.spyOn(mockTasksApiService, 'saveRequestTaskAction');
    submit();

    expect(apiServiceSpy).toHaveBeenCalledTimes(1);
    expect(apiServiceSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION',
      }),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['confirmation'], expect.anything());
  });

  it('should report the journey outcome to analytics after the submission is confirmed', () => {
    submit();

    expect(analyticsService.push).toHaveBeenCalledTimes(1);
    expect(analyticsService.push).toHaveBeenCalledWith({
      event: 'journey_outcome',
      journey_id: 'J_016',
      task_name: 'cca_variation',
      task_outcome: 'submitted',
      variation_modifications: 'amend_70_percent_rule_evaluation|change_throughput_unit',
      variation_modification_count: 2,
    });
  });

  it('should not report a journey outcome when the submission fails', () => {
    mockTasksApiService.saveRequestTaskAction.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: ErrorCodes.UNAV1001 } })),
    );

    submit();

    expect(analyticsService.push).not.toHaveBeenCalled();
  });

  it('should emit a modification list longer than the GA4 parameter limit', () => {
    store.setPayload({
      ...mockUnaVariationRequestTaskPayload,
      underlyingAgreement: {
        ...mockUnderlyingAgreementVariation,
        underlyingAgreementVariationDetails: {
          reason: 'Variation reason',
          modifications: [
            'ADDITION_OR_REMOVAL_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT',
            'AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS',
            'AMEND_FACILITY_BASE_YEAR_CHANGE_ALLOCATION',
          ],
        },
      },
    });

    submit();

    const event = analyticsService.push.mock.calls[0][0] as JourneyOutcomeEvent;

    expect(event.variation_modifications).toBe(
      'addition_or_removal_one_or_more_facilities_from_agreement|amend_operator_or_organisation_target_unit_address|amend_facility_base_year_change_allocation',
    );
    expect(event.variation_modifications.length).toBeGreaterThan(100);
    expect(event.variation_modification_count).toBe(3);
  });

  it('should report an empty modification list when no modifications were selected', () => {
    store.setPayload({
      ...mockUnaVariationRequestTaskPayload,
      underlyingAgreement: {
        ...mockUnderlyingAgreementVariation,
        underlyingAgreementVariationDetails: {
          reason: 'Variation reason',
          modifications: [],
        },
      },
    });

    submit();

    expect(analyticsService.push).toHaveBeenCalledWith({
      event: 'journey_outcome',
      journey_id: 'J_016',
      task_name: 'cca_variation',
      task_outcome: 'submitted',
      variation_modifications: '',
      variation_modification_count: 0,
    });
  });
});
