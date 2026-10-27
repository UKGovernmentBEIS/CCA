import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, ParamMap, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { SchemeVersionEnum } from '@shared/types';
import { getByLabelText, getByRole, getByText } from '@testing';

import { extractCriteria } from '../utils';
import { TransactionsFiltersComponent } from './transactions-filters.component';
import { TransactionReportFormProvider } from './transactions-filters-form.provider';

describe('TransactionsFiltersComponent', () => {
  let component: TransactionsFiltersComponent;
  let fixture: ComponentFixture<TransactionsFiltersComponent>;
  let queryParamMap$: BehaviorSubject<ParamMap>;

  beforeEach(async () => {
    queryParamMap$ = new BehaviorSubject(
      convertToParamMap({
        term: null,
        targetPeriodType: 'TP7',
        paymentStatus: 'AWAITING_PAYMENT',
      }),
    );

    const mockActivatedRoute = {
      snapshot: {
        queryParams: {
          term: null as string | null,
          targetPeriodType: 'TP7',
          paymentStatus: 'AWAITING_PAYMENT',
        },
        paramMap: convertToParamMap({}),
        queryParamMap: convertToParamMap({
          term: null,
          targetPeriodType: 'TP7',
          paymentStatus: 'AWAITING_PAYMENT',
        }),
      },
      queryParams: of({
        term: null,
        targetPeriodType: 'TP7',
        paymentStatus: 'AWAITING_PAYMENT',
      }),
      queryParamMap: queryParamMap$,
    };

    await TestBed.configureTestingModule({
      imports: [TransactionsFiltersComponent],
      providers: [
        FormBuilder,
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        TransactionReportFormProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionsFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize filtersForm with correct query parameters', () => {
    expect(component.filtersForm.value).toEqual({
      term: null,
      targetPeriodType: 'TP7',
      paymentStatus: 'AWAITING_PAYMENT',
      schemeVersion: SchemeVersionEnum.CCA_3,
    });
  });

  it('should follow the URL when the query params change without recreating the page', () => {
    // "All" is applied by removing the payment status from the URL
    queryParamMap$.next(convertToParamMap({ targetPeriodType: 'TP7' }));
    fixture.detectChanges();

    const select = getByLabelText('Status', fixture.nativeElement) as HTMLSelectElement;
    expect(select.selectedOptions[0].textContent.trim()).toBe('All');

    // e.g. the page restores the default payment status after the menu link resets the query params
    queryParamMap$.next(convertToParamMap({ paymentStatus: 'AWAITING_PAYMENT' }));
    fixture.detectChanges();

    expect(select.selectedOptions[0].textContent.trim()).toBe('Awaiting payment');
    expect(component.filtersForm.controls.paymentStatus.value).toBe('AWAITING_PAYMENT');
  });

  it('should apply the selected payment status to the API search criteria', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const select = getByLabelText('Status', fixture.nativeElement) as HTMLSelectElement;
    select.value = Array.from(select.options).find((option) => option.textContent.trim() === 'Refunded').value;
    select.dispatchEvent(new Event('change'));
    getByRole('button', { name: /Apply/i }, fixture.nativeElement).click();

    const queryParams = navigate.mock.calls[0][1].queryParams;
    expect(queryParams.paymentStatus).toBe('REFUNDED');
    expect(extractCriteria(convertToParamMap(queryParams)).paymentStatus).toBe('REFUNDED');
  });

  it('should apply the selected target period to the API search criteria', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const select = getByLabelText('Period', fixture.nativeElement) as HTMLSelectElement;
    select.value = Array.from(select.options).find((option) => option.textContent.trim() === 'TP8').value;
    select.dispatchEvent(new Event('change'));
    getByRole('button', { name: /Apply/i }, fixture.nativeElement).click();

    const queryParams = navigate.mock.calls[0][1].queryParams;
    expect(queryParams.targetPeriodType).toBe('TP8');
    expect(extractCriteria(convertToParamMap(queryParams)).targetPeriodType).toBe('TP8');
  });

  it('should list the CCA3 periods with "All" for the CCA3 scheme', () => {
    const select = getByLabelText('Period', fixture.nativeElement) as HTMLSelectElement;
    expect(Array.from(select.options).map((option) => option.textContent.trim())).toEqual(['All', 'TP7', 'TP8', 'TP9']);
  });

  it('should list the CCA2 periods and default the period to "All" when switching to the CCA2 scheme', () => {
    const schemeSelect = getByLabelText('Scheme', fixture.nativeElement) as HTMLSelectElement;
    schemeSelect.value = Array.from(schemeSelect.options).find(
      (option) => option.textContent.trim() === SchemeVersionEnum.CCA_2,
    ).value;
    schemeSelect.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    const periodSelect = getByLabelText('Period', fixture.nativeElement) as HTMLSelectElement;
    expect(Array.from(periodSelect.options).map((option) => option.textContent.trim())).toEqual(['All', 'TP6']);
    expect(periodSelect.selectedOptions[0].textContent.trim()).toBe('All');
    expect(component.filtersForm.controls.targetPeriodType.value).toBeNull();
  });

  it('should treat a period that does not belong to the scheme as "All"', () => {
    queryParamMap$.next(convertToParamMap({ schemeVersion: SchemeVersionEnum.CCA_2, targetPeriodType: 'TP7' }));
    fixture.detectChanges();

    expect(component.filtersForm.controls.targetPeriodType.value).toBeNull();
  });

  it('should reset the displayed payment status and search criteria when clearing filters', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    getByRole('button', { name: /Clear/i }, fixture.nativeElement).click();
    fixture.detectChanges();

    const select = getByLabelText('Status', fixture.nativeElement) as HTMLSelectElement;
    expect(select.selectedOptions[0].textContent.trim()).toBe('Awaiting payment');
    expect(component.filtersForm.controls.paymentStatus.value).toBe('AWAITING_PAYMENT');
    expect(component.filtersForm.controls.targetPeriodType.value).toBeNull();
    expect(extractCriteria(convertToParamMap(navigate.mock.calls[0][1].queryParams)).paymentStatus).toBe(
      'AWAITING_PAYMENT',
    );
  });

  it('should render the "Filters" panel', () => {
    const heading = getByText('Filters', fixture.nativeElement);
    expect(heading).toBeTruthy();
  });

  it('should have an input field for "Target unit ID, operator name, transaction ID or sector ID"', () => {
    const input = getByLabelText(
      /Target unit ID, operator name, transaction ID, sector ID, facility ID or site name/i,
      fixture.nativeElement,
    );
    expect(input).toBeTruthy();
  });

  it('should have a select dropdown for "Period"', () => {
    const select = getByLabelText('Period', fixture.nativeElement);
    expect(select).toBeTruthy();
  });

  it('should have a select dropdown for "Status"', () => {
    const select = getByLabelText('Status', fixture.nativeElement);
    expect(select).toBeTruthy();
  });

  it('should have a submit button with "Apply"', () => {
    const applyButton = getByRole('button', { name: /Apply/i }, fixture.nativeElement);
    expect(applyButton).toBeTruthy();
  });

  it('should have a clear button with "Clear"', () => {
    const clearButton = getByRole('button', { name: /Clear/i }, fixture.nativeElement);
    expect(clearButton).toBeTruthy();
  });
});
