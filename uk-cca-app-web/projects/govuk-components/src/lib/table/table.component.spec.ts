import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { TableComponent } from './table.component';
import { GovukTableColumn, SortEvent } from './table.interface';

/* eslint-disable @typescript-eslint/no-unused-vars */
describe('TableComponent', () => {
  @Component({
    template: `
      <govuk-table [columns]="columns()" [data]="data()" [caption]="caption()" (sort)="onSort($event)"></govuk-table>
    `,
    imports: [TableComponent],
  })
  class TestComponent {
    columns = signal<GovukTableColumn[]>([
      { header: 'Name', field: 'name', widthClass: 'govuk-!-width-one-quarter', isHeader: true },
      { header: 'Surname', field: 'surname' },
      { header: 'Age', field: 'age' },
    ]);

    data = signal<any[]>([]);
    caption = signal<string | null>(null);

    onSort = vi.fn((_: SortEvent) => null);
  }

  @Component({
    template: `
      <govuk-table [columns]="columns()" [data]="data()" [caption]="caption()" (sort)="onSort($event)">
        <ng-template let-column="column" let-row="row">
          @if (column.field === 'link') {
            <a>{{ row[column.field] }}</a>
          } @else {
            {{ row[column.field] }}
          }
        </ng-template>
      </govuk-table>
    `,
    imports: [TableComponent],
  })
  class TestTemplateComponent {
    columns = signal<GovukTableColumn[]>([
      { header: 'Link', field: 'link' },
      { header: 'Text', field: 'text' },
    ]);

    data = signal<any[]>([]);
    caption = signal<string | null>(null);

    onSort = vi.fn((_: SortEvent) => null);
  }

  let component: TableComponent<any>;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableComponent, TestComponent, TestTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(TableComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the caption', () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    let caption = hostElement.querySelector<HTMLTableCaptionElement>('caption');

    expect(caption).toBeNull();

    hostComponent.caption.set('Test Caption');
    fixture.detectChanges();

    caption = hostElement.querySelector<HTMLTableCaptionElement>('caption');

    expect(caption).not.toBeNull();
    expect(caption.textContent).toContain('Test Caption');
  });

  it('should render the headers', () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const thead = hostElement.querySelector<HTMLElement>('thead');

    expect(thead).toBeTruthy();

    const headers = thead.querySelectorAll<HTMLTableCellElement>('th');

    expect(headers[0].innerHTML).toContain('Name');
    expect(headers[1].innerHTML).toContain('Surname');
    expect(headers[2].innerHTML).toContain('Age');
  });

  it('should render a header hint as a regular-weight line below the header', () => {
    hostComponent.columns.set([
      { header: 'Certified status', field: 'certificationStatus', headerHint: '1/7/25 – 30/6/27' },
      { header: 'Sortable', field: 'sortable', headerHint: 'Sortable hint', isSortable: true },
    ]);

    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const headers = hostElement.querySelectorAll<HTMLTableCellElement>('thead th');

    expect(hostElement.querySelector('table')?.classList.contains('govuk-table--header-hint')).toBe(true);
    expect(headers[0].textContent).toContain('Certified status');
    expect(headers[0].querySelector('.govuk-\\!-display-block.govuk-\\!-font-weight-regular')?.textContent).toContain(
      '1/7/25 – 30/6/27',
    );
    expect(headers[1].querySelector('.govuk-\\!-display-block.govuk-\\!-font-weight-regular')?.textContent).toContain(
      'Sortable hint',
    );
  });

  it('should render the data', () => {
    hostComponent.data.set([
      { name: 'Name 1', surname: 'Surname 1', age: 23 },
      { name: 'Name 2', surname: 'Surname 2', age: 48 },
      { name: 'Name 3', surname: 'Surname 3', age: 32 },
    ]);

    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const tbody = hostElement.querySelector<HTMLElement>('tbody');

    expect(tbody).toBeTruthy();

    const rows = tbody.querySelectorAll<HTMLTableRowElement>('tr');

    expect(rows.length).toEqual(3);

    expect(rows[0].querySelector<HTMLTableCellElement>('th').textContent).toContain('Name 1');
    expect(rows[2].querySelector<HTMLTableCellElement>('th').textContent).toContain('Name 3');

    expect(rows[1].querySelectorAll<HTMLTableCellElement>('td')[0].textContent).toContain('Surname 2');
    expect(rows[2].querySelectorAll<HTMLTableCellElement>('td')[1].textContent).toContain('32');
  });

  it('should assign width classes', () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const thead = hostElement.querySelector<HTMLElement>('thead');
    const headers = thead.querySelectorAll<HTMLTableCellElement>('th');

    expect(headers[0].classList).toContain('govuk-!-width-one-quarter');
  });

  it('should assign numeric class', () => {
    hostComponent.data.set([
      { name: 'Name 1', surname: 'Surname 1', age: 23 },
      { name: 'Name 2', surname: 'Surname 2', age: 48 },
      { name: 'Name 3', surname: 'Surname 3', age: 32 },
    ]);

    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const tds = hostElement.querySelectorAll<HTMLTableCellElement>('td');

    expect(tds[0].classList).not.toContain('govuk-table__cell--numeric');
    expect(tds[1].classList).toContain('govuk-table__cell--numeric');
    expect(tds[2].classList).not.toContain('govuk-table__cell--numeric');
    expect(tds[3].classList).toContain('govuk-table__cell--numeric');
  });

  it.each([false, true])('aligns declared numeric strings and their header, including row headers (%s)', (isHeader) => {
    hostComponent.columns.set([
      { header: 'ID', field: 'id' },
      { header: 'Amount', field: 'amount', isNumeric: true, isHeader, widthClass: 'govuk-!-width-one-quarter' },
    ]);
    const amounts = ['0.00', '120.50', '999,999,999,999,999,999.99', '', undefined];
    hostComponent.data.set(amounts.map((amount) => ({ id: '00123', amount })));
    fixture.detectChanges();

    const root: HTMLElement = fixture.nativeElement;
    const header = root.querySelector('thead th:last-child');
    expect(header.classList).toContain('govuk-table__header--numeric');
    expect(header.classList).toContain('govuk-!-width-one-quarter');
    const cells = Array.from(root.querySelectorAll('tbody tr > :last-child'));
    expect(cells.map((cell) => cell.textContent.trim())).toEqual(amounts.map((amount) => amount ?? ''));
    for (const cell of cells) {
      expect(cell.classList).toContain(isHeader ? 'govuk-table__header--numeric' : 'govuk-table__cell--numeric');
    }
    expect(root.querySelector('tbody td:first-child').classList).not.toContain('govuk-table__cell--numeric');
  });

  it('preserves numeric header classes set by existing consumers through widthClass', () => {
    hostComponent.columns.set([{ header: 'Amount', field: 'amount', widthClass: 'govuk-table__header--numeric' }]);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('thead th').classList).toContain('govuk-table__header--numeric');
  });

  it('should display sort buttons and emit event on click', () => {
    hostComponent.columns.set([
      { header: 'One', field: 'first', isSortable: true },
      { header: 'Second', field: 'second', isSortable: true },
      { header: 'Third', field: 'third', isSortable: false },
    ]);

    hostComponent.data.set([
      { first: 1, second: new Date('2020-07-23T10:00:00Z'), third: 'abc' },
      { first: 2, second: new Date('2020-07-23T11:00:00Z'), third: 'cda' },
    ]);

    fixture.detectChanges();

    const sortButtons = fixture.debugElement.queryAll(By.css('[aria-sort] button'));
    expect(sortButtons.length).toEqual(2);

    sortButtons[0].nativeElement.click();
    fixture.detectChanges();

    expect(hostComponent.onSort).toHaveBeenCalledWith({ column: 'first', direction: 'ascending' });
    expect(sortButtons[0].parent.attributes['aria-sort']).toBe('ascending');
    expect(sortButtons[1].parent.attributes['aria-sort']).toBe('none');

    sortButtons[0].nativeElement.click();
    fixture.detectChanges();

    expect(hostComponent.onSort).toHaveBeenCalledWith({ column: 'first', direction: 'descending' });
    expect(sortButtons[0].parent.attributes['aria-sort']).toBe('descending');
    expect(sortButtons[1].parent.attributes['aria-sort']).toBe('none');

    sortButtons[1].nativeElement.click();
    fixture.detectChanges();

    expect(hostComponent.onSort).toHaveBeenCalledWith({ column: 'second', direction: 'ascending' });
    expect(sortButtons[0].parent.attributes['aria-sort']).toBe('none');
    expect(sortButtons[1].parent.attributes['aria-sort']).toBe('ascending');
  });

  it('should not expose arrow characters in sort names or live region announcements', () => {
    hostComponent.columns.set([{ header: 'Name', field: 'name', isSortable: true }]);
    hostComponent.data.set([{ name: 'A' }]);
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const button = hostElement.querySelector<HTMLButtonElement>('[aria-sort] button');
    expect(button.textContent.trim()).toBe('Name');
    expect(button.textContent).not.toMatch(/[▲▼↑↓]/);

    button.click();
    fixture.detectChanges();

    const status = hostElement.querySelector<HTMLElement>('[role="status"]');
    expect(status.textContent).toContain('Sort by Name (ascending)');
    expect(status.textContent).not.toMatch(/[▲▼↑↓]/);
  });

  it('should display custom template', () => {
    const templateFixture = TestBed.createComponent(TestTemplateComponent);
    templateFixture.componentInstance.data.set([{ link: 'Go to', text: 'Something to watch' }]);
    templateFixture.detectChanges();

    const element: HTMLElement = templateFixture.nativeElement;
    const anchors = element.querySelectorAll('a');

    expect(anchors.length).toEqual(1);
    expect(anchors[0].textContent).toEqual('Go to');
  });
});
