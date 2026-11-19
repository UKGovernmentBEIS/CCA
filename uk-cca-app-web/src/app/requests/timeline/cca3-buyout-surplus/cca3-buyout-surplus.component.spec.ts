import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, Router } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { getByLabelText, getByRole } from '@testing';

import { Cca3BuyoutSurplusComponent } from './cca3-buyout-surplus.component';
import { FacilitiesListComponent } from './facilities-list.component';
import { FACILITIES_LIST_FORM_PROVIDER } from './facilities-list-form.provider';
import { mockAccountAction, mockAccountAction126, mockAccountPayload, mockFacility } from './testing/mock-data';

describe('Cca3BuyoutSurplusComponent', () => {
  let fixture: ComponentFixture<Cca3BuyoutSurplusComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cca3BuyoutSurplusComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    store = TestBed.inject(RequestActionStore);
    store.setAction(mockAccountAction());
    fixture = TestBed.createComponent(Cca3BuyoutSurplusComponent);
    await fixture.whenStable();
  });

  const text = (element: Element) => element.textContent.replace(/\s+/g, ' ').trim();
  const root = () => fixture.nativeElement as HTMLElement;
  const rows = () => Array.from(root().querySelectorAll<HTMLTableRowElement>('tbody tr'));
  const cells = (row: Element) => Array.from(row.querySelectorAll('td')).map(text);

  function search(value: string) {
    const input = getByLabelText('Facility ID or site name') as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  function select(label: string, optionText: string) {
    const select = getByLabelText(label) as HTMLSelectElement;
    select.value = Array.from(select.options).find((option) => option.text === optionText).value;
    select.dispatchEvent(new Event('change'));
  }

  async function click(name: string) {
    getByRole('button', { name }).click();
    await fixture.whenStable();
  }

  it('renders Run ID, eight fixed columns and correctly ordered historical rows', () => {
    expect(text(fixture.nativeElement)).toContain('BOS-TP8004');
    expect(Array.from(root().querySelectorAll('thead th')).map(text)).toEqual([
      'Site name',
      'Facility ID',
      'TP',
      'TPR Result',
      'Surplus used (tCO₂e)',
      'Buy-out amount (GBP)',
      'Overpayment buy-out amount (GBP)',
      'Surplus gained (tCO₂e)',
    ]);
    expect(rows().map(cells)).toEqual([
      ['West London Site', 'AIC-F00064', '7', 'Target not met', '12', '202.00', '', ''],
      ['West London Site', 'AIC-F00064', '8', 'Target not met', '12', '1,001.12', '', ''],
      ['North London Site', 'AIC-F00065', '8', 'Target met', '', '', '199.25', '0.0000000'],
      ['East London Site', 'AIC-F00066', '9', 'Target met', '', '', '', '15'],
    ]);
    for (const columnIndex of [5, 6, 7, 8]) {
      expect(root().querySelector(`thead th:nth-child(${columnIndex})`).classList).toContain(
        'govuk-table__header--numeric',
      );
      for (const row of rows()) {
        expect(row.querySelector(`td:nth-child(${columnIndex})`).classList).toContain('govuk-table__cell--numeric');
      }
    }
    expect(rows().map((row) => row.querySelector('a')?.getAttribute('href') ?? null)).toEqual([
      '/cca3-buyout-surplus/facility/AIC-F00064/TP7',
      '/cca3-buyout-surplus/facility/AIC-F00064/TP8',
      '/cca3-buyout-surplus/facility/AIC-F00065/TP8',
      '/cca3-buyout-surplus/facility/AIC-F00066/TP9',
    ]);
    expect(
      Array.from((getByLabelText('Target period') as HTMLSelectElement).options).map((option) => option.text),
    ).toEqual(['All', 'TP7', 'TP8', 'TP9']);
  });

  it('renders the TP8 summary, notice, recipients and facility settlement', async () => {
    store.setAction(mockAccountAction126);
    await fixture.whenStable();

    expect(Array.from(root().querySelectorAll('h2')).map(text)).toEqual([
      'Details',
      'TP8 Buy-out',
      'Recipients',
      'Facilities',
    ]);
    const summaryRows = Array.from(root().querySelectorAll('cca-summary .govuk-summary-list__row'));
    expect(
      summaryRows
        .filter((row) => text(row.querySelector('dt')) !== 'Users')
        .map((row) => [text(row.querySelector('dt')), text(row.querySelector('dd'))]),
    ).toEqual([
      ['Run ID', 'BOS-TP8007'],
      ['Cost (GBP / tCO₂e)', '11.00'],
      ['Buy-out MoA file', 'CCA080012.pdf (opens in a new tab)'],
      ['Total facilities', '1'],
      ['Total buy-out fee (GBP)', '275.00'],
      ['Due date', '27 Oct 2026'],
    ]);
    const notice = getByRole('link', { name: 'CCA080012.pdf (opens in a new tab)' }) as HTMLAnchorElement;
    expect(notice.getAttribute('href')).toBe('/file-download/document/00000000-0000-4000-8000-000000000126');
    expect(notice.target).toBe('_blank');
    const recipientRow = summaryRows.find((row) => text(row.querySelector('dt')) === 'Users');
    expect(Array.from(recipientRow.querySelectorAll('dd span')).map(text)).toEqual([
      'John William, Responsible person, williamsj@example.com',
      'Matthew Johnson, Administrative contact, mjohnson@example.com',
    ]);
    expect(rows().map(cells)).toEqual([
      ['test facility 2', 'ADS_1-F00001', '8', 'Target not met', '0', '275.00', '', ''],
    ]);
    expect(
      Array.from((getByLabelText('Target period') as HTMLSelectElement).options).map((option) => option.text),
    ).toEqual(['All', 'TP8']);
    expect(text(root())).not.toContain('825');
  });

  it('applies filters together only on Apply and leaves account totals, documents and recipients unchanged', async () => {
    const summaryBefore = fixture.nativeElement.querySelector('cca-summary').innerHTML;
    search('  wEsT lOn  ');
    select('Target period', 'TP8');
    select('TPR Result', 'Target not met');
    await fixture.whenStable();
    expect(rows()).toHaveLength(4);
    await click('Apply');
    expect(rows().map(cells)).toEqual([
      ['West London Site', 'AIC-F00064', '8', 'Target not met', '12', '1,001.12', '', ''],
    ]);
    expect(fixture.nativeElement.querySelector('cca-summary').innerHTML).toBe(summaryBefore);
    expect((getByLabelText('Target period') as HTMLSelectElement).options).toHaveLength(4);
    await click('Clear');
    expect(rows()).toHaveLength(4);
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('');
    expect((getByLabelText('Target period') as HTMLSelectElement).selectedOptions[0].text).toBe('All');
    expect((getByLabelText('TPR Result') as HTMLSelectElement).selectedOptions[0].text).toBe('All');
  });

  it('keeps all headers and the exact empty message without stale pagination', async () => {
    search('unknown facility');
    await click('Apply');
    expect(rows()).toHaveLength(0);
    expect(fixture.nativeElement.querySelectorAll('thead th')).toHaveLength(8);
    expect(text(fixture.nativeElement.querySelector('[role="status"]'))).toBe('No results match the filter criteria.');
    expect(fixture.nativeElement.querySelector('cca-pagination')).toBeNull();
    await click('Clear');
    expect(fixture.nativeElement.querySelector('cca-pagination')).not.toBeNull();
  });

  it('updates the form and rows from query parameters without emitting form changes or navigating again', async () => {
    const form = fixture.debugElement
      .query(By.directive(FacilitiesListComponent))
      .injector.get(FACILITIES_LIST_FORM_PROVIDER);
    const formChanges = vi.fn();
    const subscription = form.valueChanges.subscribe(formChanges);
    const router = TestBed.inject(Router);
    const navigate = vi.spyOn(router, 'navigate');

    await router.navigateByUrl('/?search=North&targetPeriod=TP8&result=TARGET_MET&pageSize=20');
    await fixture.whenStable();

    expect(form.getRawValue()).toEqual({ search: 'North', targetPeriod: 'TP8', result: 'TARGET_MET' });
    expect(cells(rows()[0])[1]).toBe('AIC-F00065');
    expect(rows()).toHaveLength(1);
    expect(formChanges).not.toHaveBeenCalled();
    expect(navigate).not.toHaveBeenCalled();
    subscription.unsubscribe();
  });

  it('paginates with the existing choices and resets page 1 on filters and page-size changes', async () => {
    store.setAction(
      mockAccountAction(
        mockAccountPayload({
          facilities: Array.from({ length: 23 }, (_, index) =>
            mockFacility({ facilityBusinessId: `AIC-F${String(index + 1).padStart(5, '0')}` }),
          ),
        }),
      ),
    );
    await fixture.whenStable();
    expect(rows()).toHaveLength(10);
    await click('Go to next page');
    expect(cells(rows()[0])[1]).toBe('AIC-F00011');
    expect(text(fixture.nativeElement.querySelector('#show-items'))).toBe('Showing 11 to 20 from 23 results');
    select('Rows per page', '20');
    await fixture.whenStable();
    expect(rows()).toHaveLength(20);
    expect(cells(rows()[0])[1]).toBe('AIC-F00001');
    await click('Go to next page');
    expect(rows()).toHaveLength(3);
    search('f00001');
    await click('Apply');
    expect(text(fixture.nativeElement.querySelector('#show-items'))).toBe('Showing 1 to 1 from 1 results');
    await click('Clear');
    expect(rows()).toHaveLength(20);
    expect(text(fixture.nativeElement.querySelector('#show-items'))).toBe('Showing 1 to 20 from 23 results');
  });

  it.each(['0', '-1', '2.5', 'NaN', 'Infinity', '9007199254740992'])(
    'defaults invalid URL pagination to page 1 and the approved page size: %s',
    async (page) => {
      store.setAction(
        mockAccountAction(
          mockAccountPayload({
            facilities: Array.from({ length: 23 }, (_, index) =>
              mockFacility({ facilityBusinessId: `AIC-F${String(index + 1).padStart(5, '0')}` }),
            ),
          }),
        ),
      );

      await TestBed.inject(Router).navigateByUrl(`/?page=${page}&pageSize=15&targetPeriod=unknown&result=SUBMITTED`);
      await fixture.whenStable();

      expect(rows()).toHaveLength(10);
      expect(cells(rows()[0])[1]).toBe('AIC-F00001');
      expect((getByLabelText('Target period') as HTMLSelectElement).selectedOptions[0].text).toBe('All');
      expect((getByLabelText('TPR Result') as HTMLSelectElement).selectedOptions[0].text).toBe('All');
      expect(text(fixture.nativeElement.querySelector('#show-items'))).toBe('Showing 1 to 10 from 23 results');
    },
  );

  it('retains unapplied form edits when changing page and clamps a stale page from the URL', async () => {
    store.setAction(
      mockAccountAction(
        mockAccountPayload({
          facilities: Array.from({ length: 23 }, (_, index) =>
            mockFacility({ facilityBusinessId: `AIC-F${String(index + 1).padStart(5, '0')}` }),
          ),
        }),
      ),
    );
    await fixture.whenStable();
    search('unapplied search');

    await click('Go to next page');

    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('unapplied search');
    expect(cells(rows()[0])[1]).toBe('AIC-F00011');
    expect(TestBed.inject(Router).parseUrl(TestBed.inject(Router).url).queryParams).toEqual({ page: '2' });

    await TestBed.inject(Router).navigateByUrl('/?page=999');
    await fixture.whenStable();
    expect(text(fixture.nativeElement.querySelector('#show-items'))).toBe('Showing 21 to 23 from 23 results');
  });

  it('supports every approved page size from query parameters', async () => {
    for (const pageSize of [10, 20, 30, 40, 50, 100]) {
      await TestBed.inject(Router).navigateByUrl(`/?pageSize=${pageSize}`);
      await fixture.whenStable();
      expect((getByLabelText('Rows per page') as HTMLSelectElement).value).toBe(String(pageSize));
    }
  });

  it('keeps the TP filter for a single-period event with no transactions', async () => {
    store.setAction(
      mockAccountAction(
        mockAccountPayload({
          summary: [],
          facilities: [
            mockFacility({
              transactionCode: undefined,
              invoicedBuyOutFee: '0.0000000',
            }),
          ],
        }),
      ),
    );
    await fixture.whenStable();
    expect(Array.from(root().querySelectorAll('h2')).map(text)).toEqual(['Details', 'Facilities']);
    expect(
      Array.from((getByLabelText('Target period') as HTMLSelectElement).options).map((option) => option.text),
    ).toEqual(['All', 'TP7']);
    expect(cells(rows()[0])[5]).toBe('');
  });

  it('does not re-read off-page facilities to filter or sort when only the page changes', async () => {
    const readFirstSiteName = vi.fn(() => 'First site');
    const facilities = Array.from({ length: 23 }, (_, index) =>
      mockFacility({ facilityBusinessId: `AIC-F${String(index + 1).padStart(5, '0')}` }),
    );
    Object.defineProperty(facilities[0], 'siteName', { get: readFirstSiteName, enumerable: true });
    store.setAction(mockAccountAction(mockAccountPayload({ facilities })));
    await fixture.whenStable();
    search('site');
    await click('Apply');
    readFirstSiteName.mockClear();

    await click('Go to next page');

    expect(cells(rows()[0])[1]).toBe('AIC-F00011');
    expect(readFirstSiteName).not.toHaveBeenCalled();
  });
});
