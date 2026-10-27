import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { Cca3TransactionDetailsFiltersComponent } from './cca3-transaction-details-filters.component';

describe('Cca3TransactionDetailsFiltersComponent', () => {
  let component: Cca3TransactionDetailsFiltersComponent;
  let fixture: ComponentFixture<Cca3TransactionDetailsFiltersComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
      imports: [Cca3TransactionDetailsFiltersComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Cca3TransactionDetailsFiltersComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should default the status filter to all', () => {
    expect(component['filtersForm'].value.paymentStatus).toBeNull();
  });

  it('should not navigate when the search term is shorter than 3 characters', () => {
    const navigate = vi.spyOn(router, 'navigate');
    component['filtersForm'].patchValue({ term: 'ab' });

    component.onApplyFilters();

    expect(component['filtersForm'].invalid).toBe(true);
    expect(navigate).not.toHaveBeenCalled();
  });

  it('should navigate with the filters and reset the page when applying', () => {
    const navigate = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    component['filtersForm'].patchValue({ term: 'Cardiff', paymentStatus: 'UNDER_APPEAL' });

    component.onApplyFilters();

    expect(navigate).toHaveBeenCalledWith([], {
      queryParams: { term: 'Cardiff', paymentStatus: 'UNDER_APPEAL', page: 1 },
      queryParamsHandling: 'merge',
      relativeTo: expect.anything(),
    });
  });

  it('should clear the filters and reset the page', () => {
    const navigate = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    component['filtersForm'].patchValue({ term: 'Cardiff', paymentStatus: 'UNDER_APPEAL' });

    component.onClearFilters();

    expect(component['filtersForm'].value).toEqual({ term: null, paymentStatus: null });
    expect(navigate).toHaveBeenCalledWith([], {
      queryParams: { term: null, paymentStatus: null, page: 1 },
      queryParamsHandling: 'merge',
      relativeTo: expect.anything(),
    });
  });
});
