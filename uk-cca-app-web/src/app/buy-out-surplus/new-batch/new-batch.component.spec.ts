import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { getByText, queryByText } from '@testing';
import { Mock, Mocked } from 'vitest';

import { AvailableTargetPeriodsBuyOutDTO, BuyOutAndSurplusInfoService, RequestsService } from 'cca-api';

import { NewBatchComponent } from './new-batch.component';

describe('NewBatchComponent', () => {
  let component: NewBatchComponent;
  let fixture: ComponentFixture<NewBatchComponent>;
  let buyOutAndSurplusInfoService: Partial<Mocked<BuyOutAndSurplusInfoService>>;
  let requestsService: Partial<Mocked<RequestsService>>;
  let router: { navigate: Mock };

  const mockExcludedAccounts = [
    { accountId: 1, businessId: 'ADS_1-T00001', name: 'tu1-oper1' },
    { accountId: 2, businessId: 'ADS_1-T00002', name: 'tu1-oper2' },
  ];

  // a single available target period, the batch applies it without asking the regulator
  const autoTargetPeriods: AvailableTargetPeriodsBuyOutDTO = {
    currentTargetPeriods: [{ id: 9, businessId: 'TP9', buyOutCost: 41 }],
    previousTargetPeriods: [
      { id: 7, businessId: 'TP7', buyOutCost: 37 },
      { id: 8, businessId: 'TP8', buyOutCost: 38 },
    ],
  };

  // the CCA2 / CCA3 overlap, the regulator picks between TP6 and TP7
  const overlappingTargetPeriods: AvailableTargetPeriodsBuyOutDTO = {
    currentTargetPeriods: [
      { id: 6, businessId: 'TP6' },
      { id: 7, businessId: 'TP7', buyOutCost: 37 },
    ],
    previousTargetPeriods: [],
  };

  const activatedRouteStub = {
    snapshot: {
      data: {} as { availableTargetPeriods: AvailableTargetPeriodsBuyOutDTO },
    },
  };

  const costRows = () =>
    Array.from(document.querySelectorAll('govuk-table')).flatMap((table) =>
      Array.from(table.querySelectorAll('tbody tr')).map((row) =>
        Array.from(row.querySelectorAll('th,td')).map((cell) => cell.textContent.replace(/\s+/g, ' ').trim()),
      ),
    );

  const sendBatch = () => {
    getByText('Send buy-out and surplus batch').click();
    fixture.detectChanges();
  };

  const createActionPayload = () => requestsService.processRequestCreateAction.mock.calls[0][0];

  beforeEach(async () => {
    buyOutAndSurplusInfoService = {
      getExcludedAccountsForBuyOutSurplusRun: vi.fn().mockReturnValue(of(mockExcludedAccounts)),
    };

    requestsService = {
      processRequestCreateAction: vi.fn().mockReturnValue(of({ requestId: 'BOS-TP9001' })),
    };

    router = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [NewBatchComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: router },
        { provide: BuyOutAndSurplusInfoService, useValue: buyOutAndSurplusInfoService },
        { provide: RequestsService, useValue: requestsService },
      ],
    }).compileComponents();
  });

  function createComponent(availableTargetPeriods: AvailableTargetPeriodsBuyOutDTO = autoTargetPeriods) {
    activatedRouteStub.snapshot.data.availableTargetPeriods = availableTargetPeriods;

    fixture = TestBed.createComponent(NewBatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  function selectTargetPeriod(targetPeriod: string) {
    const select = document.querySelector<HTMLSelectElement>('select');
    select.value = Array.from(select.options).find((option) => option.textContent.trim() === targetPeriod).value;
    select.dispatchEvent(new Event('change', { bubbles: true }));
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should contain appropriate content', () => {
    createComponent();

    expect(getByText('New buy-out and surplus batch')).toBeTruthy();

    expect(getByText('Target units on hold')).toBeTruthy();

    expect(
      getByText(
        'Target units below will not be included in this batch. In case you want to change their status, you can do so from the respective target unit account page on the "Buy-out and surplus" tab.',
      ),
    ).toBeTruthy();

    expect(getByText(/This list represents a current snapshot/)).toBeTruthy();

    expect(getByText('Send buy-out and surplus batch')).toBeTruthy();
  });

  it('should populate with correct data', () => {
    createComponent();

    const excludedAccountsTable = document.querySelectorAll('govuk-table')[1];

    expect(excludedAccountsTable.querySelectorAll('.govuk-table__row')).toHaveLength(mockExcludedAccounts.length + 1);
  });

  describe('with a single available target period', () => {
    beforeEach(() => createComponent());

    it('should apply it without offering a choice', () => {
      expect(document.querySelector('select')).toBeNull();
      expect(queryByText('Target period')).toBeNull();

      expect(buyOutAndSurplusInfoService.getExcludedAccountsForBuyOutSurplusRun).toHaveBeenCalledWith('TP9');
    });

    it('should name it in the introduction', () => {
      expect(document.body.textContent).toContain(
        'calculates the amount of buy-out or surplus for TP9 for each eligible facility',
      );
    });

    it('should list its cost together with the previous target periods, latest first', () => {
      expect(costRows().slice(0, 3)).toEqual([
        ['TP9', '£41 / tCO2e'],
        ['TP8', '£38 / tCO2e'],
        ['TP7', '£37 / tCO2e'],
      ]);
    });
  });

  describe('with overlapping target periods', () => {
    beforeEach(() => createComponent(overlappingTargetPeriods));

    it('should offer the latest one as the default choice', () => {
      const select = document.querySelector<HTMLSelectElement>('select');

      expect(Array.from(select.options).map((option) => option.textContent.trim())).toEqual(['TP7', 'TP6']);
      expect(component['form'].controls.targetPeriodType.value).toBe('TP7');

      expect(buyOutAndSurplusInfoService.getExcludedAccountsForBuyOutSurplusRun).toHaveBeenCalledWith('TP7');
    });

    it('should show the cost of the selected CCA3 target period only', () => {
      expect(getByText('Cost')).toBeTruthy();
      expect(costRows().slice(0, 1)).toEqual([['TP7', '£37 / tCO2e']]);
    });

    it('should hide the cost and refetch the accounts on hold when a CCA2 target period is selected', () => {
      selectTargetPeriod('TP6');

      expect(queryByText('Cost')).toBeNull();
      expect(buyOutAndSurplusInfoService.getExcludedAccountsForBuyOutSurplusRun).toHaveBeenCalledWith('TP6');
    });

    it('should recompute the derived state every time the selection changes', () => {
      expect(component['hasTargetPeriodChoice']()).toBe(true);
      expect(component['targetPeriodOptions']()).toEqual([
        { value: 'TP7', text: 'TP7' },
        { value: 'TP6', text: 'TP6' },
      ]);

      expect(component['selectedTargetPeriod']()).toBe('TP7');
      expect(component['isCca3Run']()).toBe(true);
      expect(component['buyOutCosts']()).toEqual([{ id: 7, businessId: 'TP7', buyOutCost: 37 }]);

      selectTargetPeriod('TP6');

      expect(component['selectedTargetPeriod']()).toBe('TP6');
      expect(component['isCca3Run']()).toBe(false);
      expect(component['buyOutCosts']()).toEqual([]);

      selectTargetPeriod('TP7');

      expect(component['selectedTargetPeriod']()).toBe('TP7');
      expect(component['isCca3Run']()).toBe(true);
      expect(component['buyOutCosts']()).toEqual([{ id: 7, businessId: 'TP7', buyOutCost: 37 }]);
    });
  });

  it('should never offer a choice when a single target period is available', () => {
    createComponent();

    expect(component['hasTargetPeriodChoice']()).toBe(false);
    expect(component['targetPeriodOptions']()).toEqual([{ value: 'TP9', text: 'TP9' }]);
  });

  it('should keep a CCA2 target period out of the cost rows', () => {
    createComponent({
      currentTargetPeriods: [{ id: 7, businessId: 'TP7', buyOutCost: 37 }],
      previousTargetPeriods: [{ id: 6, businessId: 'TP6', buyOutCost: 36 }],
    });

    expect(component['buyOutCosts']()).toEqual([{ id: 7, businessId: 'TP7', buyOutCost: 37 }]);
  });

  it('should not request the accounts on hold when no target period is available', () => {
    createComponent({ currentTargetPeriods: [], previousTargetPeriods: [] });

    expect(component['selectedTargetPeriod']()).toBeNull();
    expect(component['isCca3Run']()).toBe(false);
    expect(buyOutAndSurplusInfoService.getExcludedAccountsForBuyOutSurplusRun).not.toHaveBeenCalled();
    expect(getByText('There are no target units on hold.')).toBeTruthy();
  });

  it('should mark a target period without a cost as not yet defined', () => {
    createComponent({
      currentTargetPeriods: [{ id: 9, businessId: 'TP9', buyOutCost: null }],
      previousTargetPeriods: [],
    });

    expect(costRows().slice(0, 1)).toEqual([['TP9', 'Not yet defined']]);
  });

  it('should tell the user when there are no accounts on hold', () => {
    buyOutAndSurplusInfoService.getExcludedAccountsForBuyOutSurplusRun.mockReturnValue(of([]));

    createComponent();

    expect(getByText('There are no target units on hold.')).toBeTruthy();
  });

  it('should tag every excluded account as on hold', () => {
    createComponent();

    const excludedAccountsTable = document.querySelectorAll('govuk-table')[1];

    expect(excludedAccountsTable.querySelectorAll('govuk-tag')).toHaveLength(mockExcludedAccounts.length);
  });

  describe('introduction', () => {
    it('should describe a CCA3 run at facility level, naming the target period', () => {
      createComponent();

      expect(document.body.textContent).toContain(
        'calculates the amount of buy-out or surplus for TP9 for each eligible facility',
      );
    });

    it('should describe a CCA2 run at target unit level, naming the target period', () => {
      createComponent({ currentTargetPeriods: [{ id: 6, businessId: 'TP6' }], previousTargetPeriods: [] });

      expect(document.body.textContent).toContain('send buy-out notifications to all eligible target units for TP6');
      expect(document.body.textContent).not.toContain('for each eligible facility');
    });

    it('should name no target period while the regulator can still change it', () => {
      createComponent(overlappingTargetPeriods);

      expect(
        getByText(
          'You are about to calculate the buy-out and surplus and send buy-out notifications to all eligible target units.',
        ),
      ).toBeTruthy();
    });
  });

  describe('triggering the batch', () => {
    it('should trigger the CCA3 workflow for the selected CCA3 target period', () => {
      createComponent();

      sendBatch();

      expect(createActionPayload()).toEqual({
        requestType: 'BUY_OUT_SURPLUS_FACILITY_RUN',
        requestCreateActionPayload: {
          payloadType: 'BUY_OUT_SURPLUS_FACILITY_RUN_CREATE_ACTION_PAYLOAD',
          targetPeriodType: 'TP9',
        },
      });
    });

    it('should trigger the CCA2 workflow when the overlapping CCA2 target period is selected', () => {
      createComponent(overlappingTargetPeriods);

      selectTargetPeriod('TP6');
      sendBatch();

      expect(createActionPayload()).toEqual({
        requestType: 'BUY_OUT_SURPLUS_RUN',
        requestCreateActionPayload: {
          payloadType: 'BUY_OUT_SURPLUS_RUN_CREATE_ACTION_PAYLOAD',
          targetPeriodType: 'TP6',
        },
      });
    });

    it('should navigate to the confirmation with the new run id', () => {
      createComponent();

      sendBatch();

      expect(router.navigate).toHaveBeenCalledWith(
        ['..', 'confirmation'],
        expect.objectContaining({ queryParams: { referenceCode: 'BOS-TP9001' } }),
      );
    });

    it('should send the regulator to the error page when a run is already in progress', () => {
      requestsService.processRequestCreateAction.mockReturnValue(
        throwError(() => ({ error: { data: [{ valid: false, requests: ['BUY_OUT_SURPLUS_FACILITY_RUN'] }] } })),
      );

      createComponent();
      sendBatch();

      expect(router.navigate).toHaveBeenCalledWith(
        ['..', 'request-error'],
        expect.objectContaining({ queryParams: { errorCode: 'inProgress' } }),
      );
    });

    it('should offer no button when there is no target period to run against', () => {
      createComponent({ currentTargetPeriods: [], previousTargetPeriods: [] });

      expect(queryByText('Send buy-out and surplus batch')).toBeNull();
    });
  });

  describe('when a buy-out cost is missing', () => {
    const missingLatestCost: AvailableTargetPeriodsBuyOutDTO = {
      currentTargetPeriods: [{ id: 9, businessId: 'TP9', buyOutCost: null }],
      previousTargetPeriods: [{ id: 8, businessId: 'TP8', buyOutCost: 38 }],
    };

    it('should block the run and name the target period that needs a cost', () => {
      createComponent(missingLatestCost);

      sendBatch();

      expect(requestsService.processRequestCreateAction).not.toHaveBeenCalled();
      expect(getByText('You must first set the TP9 cost in order for the batch run to proceed.')).toBeTruthy();
    });

    it('should link to the change cost page of that target period', () => {
      createComponent(missingLatestCost);

      sendBatch();

      expect(component['errorSummaryInfo']()).toEqual(
        expect.objectContaining({ link: '/buyout-surplus/cost/TP9', linkText: 'Set the TP9 buy-out cost' }),
      );
      expect(document.querySelector('.govuk-error-summary a').textContent.trim()).toBe('Set the TP9 buy-out cost');
    });

    it('should block on a previous target period too, since the batch prices those as well', () => {
      createComponent({
        currentTargetPeriods: [{ id: 9, businessId: 'TP9', buyOutCost: 41 }],
        previousTargetPeriods: [{ id: 7, businessId: 'TP7', buyOutCost: null }],
      });

      sendBatch();

      expect(requestsService.processRequestCreateAction).not.toHaveBeenCalled();
      expect(getByText('You must first set the TP7 cost in order for the batch run to proceed.')).toBeTruthy();
    });

    it('should not block a CCA2 run, which has no buy-out cost of its own', () => {
      createComponent({ currentTargetPeriods: [{ id: 6, businessId: 'TP6' }], previousTargetPeriods: [] });

      sendBatch();

      expect(requestsService.processRequestCreateAction).toHaveBeenCalled();
      expect(document.querySelector('.govuk-error-summary')).toBeNull();
    });
  });
});
