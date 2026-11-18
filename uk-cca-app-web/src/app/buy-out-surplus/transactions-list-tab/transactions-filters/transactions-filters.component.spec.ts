import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { getByLabelText, getByRole, getByText } from '@testing';

import { TransactionsFiltersComponent } from './transactions-filters.component';
import { TransactionReportFormProvider } from './transactions-filters-form.provider';

describe('TransactionsFiltersComponent', () => {
  let component: TransactionsFiltersComponent;
  let fixture: ComponentFixture<TransactionsFiltersComponent>;

  beforeEach(async () => {
    const mockActivatedRoute = {
      snapshot: {
        queryParams: {
          term: null,
          targetPeriodType: 'TP6',
          buyOutSurplusPaymentStatus: 'PAID',
        },
        paramMap: convertToParamMap({}),
        queryParamMap: convertToParamMap({
          term: null,
          targetPeriodType: 'TP6',
          buyOutSurplusPaymentStatus: 'PAID',
        }),
      },
      queryParams: of({
        term: null,
        targetPeriodType: 'TP6',
        buyOutSurplusPaymentStatus: 'PAID',
      }),
      queryParamMap: of(
        convertToParamMap({
          term: null,
          targetPeriodType: 'TP6',
          buyOutSurplusPaymentStatus: 'PAID',
        }),
      ),
    };

    await TestBed.configureTestingModule({
      imports: [TransactionsFiltersComponent],
      providers: [
        FormBuilder,
        provideHttpClient(),
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
      targetPeriodType: 'TP6',
      buyOutSurplusPaymentStatus: 'PAID',
    });
  });

  it('should render the "Filters" panel', () => {
    const heading = getByText('Filters', fixture.nativeElement);
    expect(heading).toBeTruthy();
  });

  it('should have an input field for "Target unit ID, operator name, transaction ID or sector ID"', () => {
    const input = getByLabelText(/Target unit ID, operator name, transaction ID or sector ID/i, fixture.nativeElement);
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
