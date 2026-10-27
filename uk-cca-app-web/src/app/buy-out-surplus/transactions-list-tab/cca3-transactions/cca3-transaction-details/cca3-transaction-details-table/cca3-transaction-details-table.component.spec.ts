import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { Mock } from 'vitest';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import {
  mockFacilityTransactionDetailsEmpty,
  mockFacilityTransactionDetailsPageOne,
} from '../../../testing/cca3-facility-mock-data';
import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';
import { Cca3TransactionDetailsTableComponent } from './cca3-transaction-details-table.component';

describe('Cca3TransactionDetailsTableComponent', () => {
  let component: Cca3TransactionDetailsTableComponent;
  let fixture: ComponentFixture<Cca3TransactionDetailsTableComponent>;
  let activatedRoute: ActivatedRouteStub;
  let mockService: { getFacilityBuyOutSurplusTransactionDetails: Mock };

  // Reversed so that the component has to restore the Facility ID order itself.
  const reversedDetailsResponse = {
    ...mockFacilityTransactionDetailsPageOne,
    transactionDetails: [...mockFacilityTransactionDetailsPageOne.transactionDetails].reverse(),
  };

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub(
      { transactionId: '1001' },
      { term: 'Cardiff', paymentStatus: 'AWAITING_PAYMENT', page: '2', pageSize: '10' },
    );

    mockService = {
      getFacilityBuyOutSurplusTransactionDetails: vi.fn().mockReturnValue(of(reversedDetailsResponse)),
    };

    await TestBed.configureTestingModule({
      imports: [Cca3TransactionDetailsTableComponent],
      providers: [
        provideRouter([]),
        Cca3TransactionDetailsStore,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: BuyOutAndSurplusFacilityTransactionViewService, useValue: mockService },
      ],
    }).compileComponents();

    TestBed.inject(AuthStore).setUserState({ roleType: 'REGULATOR' });

    fixture = TestBed.createComponent(Cca3TransactionDetailsTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should request the transaction facilities with the criteria taken from the query params', () => {
    expect(mockService.getFacilityBuyOutSurplusTransactionDetails).toHaveBeenCalledWith(1001, {
      term: 'Cardiff',
      paymentStatus: 'AWAITING_PAYMENT',
      pageNumber: 1,
      pageSize: 10,
    });

    expect(component['currentPage']()).toBe(2);
    expect(component['pageSize']()).toBe(10);
    expect(component['totalFacilities']()).toBe(mockFacilityTransactionDetailsPageOne.total);
  });

  it('should list the facilities sorted by Facility ID ascending', () => {
    const facilityIds = component['tableData']().map((row) => row.facilityBusinessId);

    expect(facilityIds).toEqual([...facilityIds].sort());
    expect(facilityIds[0]).toBe('ADS_1-F00001');
  });

  it('should format the transaction amounts with two decimal places', () => {
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('120.00');
    expect(fixture.nativeElement.textContent).toContain('80.50');
    expect(fixture.nativeElement.textContent).toContain('0.00');
    const root: HTMLElement = fixture.nativeElement;
    expect(root.querySelector('thead th:nth-child(5)').classList).toContain('govuk-table__header--numeric');
    for (const cell of root.querySelectorAll('tbody tr td:nth-child(5)')) {
      expect(cell.classList).toContain('govuk-table__cell--numeric');
    }
  });

  it('should not allow the selection of terminated facilities', () => {
    const terminated = component['tableData']().find((row) => row.paymentStatus === 'TERMINATED');

    expect(terminated?.isSelectable).toBe(false);

    component.onSelectChange({ row: terminated, checked: true });

    expect(component['selectedFacilities']().size).toBe(0);
  });

  it('should link each nonterminated facility to its own change amount form', () => {
    fixture.detectChanges();
    const links = Array.from<HTMLAnchorElement>(fixture.nativeElement.querySelectorAll('a')).filter((link) =>
      link.textContent.includes('Change amount'),
    );
    const editableFacilities = component['tableData']().filter((facility) => facility.paymentStatus !== 'TERMINATED');

    expect(links).toHaveLength(editableFacilities.length);
    editableFacilities.forEach((facility, index) => {
      expect(links[index].getAttribute('href')).toContain(`/facilities/${facility.facilityBusinessId}/change-amount`);
    });
  });

  it('should not offer the change amount link to non regulators', () => {
    TestBed.inject(AuthStore).setUserState({ roleType: 'OPERATOR' });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('Change amount');
  });

  it('should link only facilities with history to their change history page', () => {
    fixture.detectChanges();
    const links = Array.from<HTMLAnchorElement>(fixture.nativeElement.querySelectorAll('a')).filter((link) =>
      link.textContent.includes('View history'),
    );
    const facilitiesWithHistory = component['tableData']().filter((facility) => facility.hasHistory);

    expect(links).toHaveLength(facilitiesWithHistory.length);
    facilitiesWithHistory.forEach((facility, index) => {
      expect(links[index].getAttribute('href')).toContain(`/facilities/${facility.facilityBusinessId}/change-history`);
    });
  });

  it('should allow viewing history for terminated facilities', () => {
    const store = TestBed.inject(Cca3TransactionDetailsStore);
    store.updateState({
      facilities: [{ id: 2010, facilityBusinessId: 'ADS_1-F00010', paymentStatus: 'TERMINATED', hasHistory: true }],
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('View history');
    expect(fixture.nativeElement.textContent).not.toContain('Change amount');
  });

  it('should not offer history to non regulators', () => {
    TestBed.inject(AuthStore).setUserState({ roleType: 'OPERATOR' });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('View history');
  });

  it('should select every selectable facility on the current page', () => {
    component.onSelectWholePageChange(true);

    expect(component['selectedFacilities']().size).toBe(9);
    expect(component['selectionAnnouncement']()).toBe('9 facilities selected');
  });

  it('should explain why the change status action is unavailable when nothing is selected', () => {
    component.onSelectWholePageChange(false);

    expect(component['selectionAnnouncement']()).toBe(
      'No facilities selected. Select at least one facility to change its status',
    );
  });

  it('should keep the selection and the change status action when a search returns no facilities', async () => {
    component.onSelectChange({ row: mockFacilityTransactionDetailsPageOne.transactionDetails[0], checked: true });
    mockService.getFacilityBuyOutSurplusTransactionDetails.mockReturnValue(of(mockFacilityTransactionDetailsEmpty));

    activatedRoute.setQueryParamMap({ term: 'Unknown site' });
    await fixture.whenStable();
    fixture.detectChanges();

    const changeStatusButton = Array.from<HTMLButtonElement>(fixture.nativeElement.querySelectorAll('button')).find(
      (button) => button.textContent.trim() === 'Change status',
    );

    expect(fixture.nativeElement.textContent).toContain('1 facilities currently selected.');
    expect(changeStatusButton.disabled).toBe(false);
    expect(changeStatusButton.getAttribute('aria-describedby')).toBe('selected-facilities-count');
  });

  it('should open the change status page for the selected facilities', () => {
    const router = TestBed.inject(Router);
    const navigate = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onChangeStatus();

    expect(navigate).toHaveBeenCalledWith(['change-status'], { relativeTo: activatedRoute });
  });

  it('should announce the results of the current page', () => {
    expect(component['resultsAnnouncement']()).toBe('Showing 11 to 20 of 20 results');
  });

  it('should show the empty state when no facility matches the search criteria', async () => {
    mockService.getFacilityBuyOutSurplusTransactionDetails.mockReturnValue(of(mockFacilityTransactionDetailsEmpty));

    fixture = TestBed.createComponent(Cca3TransactionDetailsTableComponent);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No results match the filter criteria.');
    expect(fixture.componentInstance['resultsAnnouncement']()).toBe('There are no results');
  });

  it('should reset the page when the page size changes', () => {
    const router = TestBed.inject(Router);
    const navigate = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onPageSizeChange(25);

    expect(navigate).toHaveBeenCalledWith([], {
      queryParams: { page: 1, pageSize: 25 },
      queryParamsHandling: 'merge',
      relativeTo: expect.anything(),
    });
  });
});
