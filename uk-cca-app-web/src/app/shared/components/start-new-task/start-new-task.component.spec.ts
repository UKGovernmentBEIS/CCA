import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, getByText } from '@testing';
import { Mock } from 'vitest';

import { RequestCreateValidationResult, RequestItemsService, RequestsService, UserStateDTO } from 'cca-api';

import { StartNewTaskComponent } from './start-new-task.component';

const userStateDTO: UserStateDTO = { userId: 'abcd-1234', roleType: 'SECTOR_USER', status: 'ACCEPTED' };

describe('StartNewTaskComponent', () => {
  let component: StartNewTaskComponent;
  let fixture: ComponentFixture<StartNewTaskComponent>;
  let authStore: AuthStore;
  let requestService: { getAvailableWorkflows: Mock };
  let requestItemService: { getItemsByRequest: Mock };

  beforeEach(async () => {
    vi.useFakeTimers({ toFake: ['Date'] });
    vi.setSystemTime(new Date('2026-12-31T23:59:59Z'));
    requestService = {
      getAvailableWorkflows: vi.fn(),
    };

    requestItemService = {
      getItemsByRequest: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [StartNewTaskComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: 1, facilityId: 2, sectorId: 3 }, null, {
            targetUnit: {
              targetUnitAccountDetails: { name: 'Target unit account 1' },
            },
          }),
        },
        { provide: RequestsService, useValue: requestService },
        { provide: RequestItemsService, useValue: requestItemService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setUserState(userStateDTO);
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  function createComponentWithMock(mockResponse: Record<string, RequestCreateValidationResult>) {
    requestService.getAvailableWorkflows.mockReturnValue(of(mockResponse));
    fixture = TestBed.createComponent(StartNewTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponentWithMock({});
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    createComponentWithMock({});
    expect(requestService.getAvailableWorkflows).toHaveBeenCalledTimes(1);
    expect(getByText('Start a facility task')).toBeTruthy();
  });

  it('should render the page heading', async () => {
    createComponentWithMock({});
    const noWorkflowsMessage = getByTestId('no-workflows');
    expect(noWorkflowsMessage).toBeTruthy();
    expect(noWorkflowsMessage.textContent).toBe('No tasks are currently available.');
  });

  it('should display workflows in correct order', () => {
    createComponentWithMock({
      FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD: { valid: true },
      PERFORMANCE_DATA_DOWNLOAD: { valid: true },
      PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD: { valid: true },
      PERFORMANCE_DATA_UPLOAD: { valid: true },
    });

    const compiled = fixture.nativeElement as HTMLElement;
    const headings = compiled.querySelectorAll('h2.govuk-heading-m');

    expect(headings.length).toBe(4);
    expect(headings[0].textContent?.trim()).toBe('PAT reporting - Upload CSV file');
    expect(headings[1].textContent?.trim()).toBe('TP reporting (TP6) - Download spreadsheets');
    expect(headings[2].textContent?.trim()).toBe('TP reporting (TP6) - Upload spreadsheets');
    expect(headings[3].textContent?.trim()).toBe('Upload PAT spreadsheets');
  });

  it('should render non-compliance workflow copy for regulator', () => {
    authStore.setUserState({ ...userStateDTO, roleType: 'REGULATOR' });
    createComponentWithMock({
      NON_COMPLIANCE: { valid: true },
    });

    expect(getByText('Non-compliance')).toBeTruthy();
    expect(
      getByText('Start a non-compliance task to record enforcement decisions relevant to this target unit.'),
    ).toBeTruthy();
    expect(getByText('Start non-compliance task')).toBeTruthy();
  });

  it('should show ETS after TP reporting and navigate to year selection', () => {
    createComponentWithMock({
      EMISSIONS_TRADING_SCHEME_DATA: { valid: true },
      PERFORMANCE_DATA_FACILITY_DIGITAL_FORM: { valid: true },
    });
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const headings = (fixture.nativeElement as HTMLElement).querySelectorAll('h2');

    expect(Array.from(headings, (heading) => heading.textContent.trim())).toEqual([
      'TP reporting',
      'UK emissions trading scheme (UK ETS) reporting - Submit form',
    ]);
    getByText('Start UK ETS reporting task').click();

    expect(navigate).toHaveBeenCalledWith(['..', 'ets-reporting'], {
      relativeTo: TestBed.inject(ActivatedRoute),
    });
    expect(requestItemService.getItemsByRequest).not.toHaveBeenCalled();
  });

  it.each(['REGULATOR', 'OPERATOR', 'VERIFIER'] as const)('should not offer ETS initiation to %s', (roleType) => {
    authStore.setUserState({ ...userStateDTO, roleType });
    createComponentWithMock({ EMISSIONS_TRADING_SCHEME_DATA: { valid: true } });

    expect(fixture.nativeElement.textContent).not.toContain('Start UK ETS reporting task');
  });

  it('should not offer ETS when it is absent from the available workflows', () => {
    createComponentWithMock({ PERFORMANCE_DATA_FACILITY_DIGITAL_FORM: { valid: true } });

    expect(fixture.nativeElement.textContent).not.toContain('Start UK ETS reporting task');
  });

  it('should offer ETS when the API makes it available regardless of the browser date', () => {
    createComponentWithMock({
      EMISSIONS_TRADING_SCHEME_DATA: { valid: true },
      PERFORMANCE_DATA_FACILITY_DIGITAL_FORM: { valid: true },
    });

    expect(fixture.nativeElement.textContent).toContain('Start UK ETS reporting task');
    expect(fixture.nativeElement.textContent).toContain('Start TP reporting task');
  });

  it('should prevent starting another ETS workflow while one is in progress', () => {
    createComponentWithMock({
      EMISSIONS_TRADING_SCHEME_DATA: {
        valid: false,
        requests: ['EMISSIONS_TRADING_SCHEME_DATA'],
      },
    });

    expect(fixture.nativeElement.textContent).toContain(
      'You cannot start the UK ETS reporting process as it is already in progress.',
    );
    expect(fixture.nativeElement.textContent).not.toContain('Start UK ETS reporting task');
  });
});
