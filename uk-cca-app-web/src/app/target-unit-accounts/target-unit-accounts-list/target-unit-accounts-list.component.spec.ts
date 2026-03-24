import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { StatusPipe } from '@shared/pipes';

import { TargetUnitAccountsListComponent } from './target-unit-accounts-list.component';
import { mockAccountSearchResults } from './testing/mock-data';

describe('TargetUnitAccountsListComponent', () => {
  let fixture: ComponentFixture<TargetUnitAccountsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TargetUnitAccountsListComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            queryParamMap: of(convertToParamMap({ term: null, page: '1' })),
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TargetUnitAccountsListComponent);
    fixture.componentRef.setInput('accounts', mockAccountSearchResults.accounts);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', async () => {
    expect(fixture.nativeElement.querySelector('[data-testid="accounts-list-table"]')).toBeTruthy();
  });

  it('should create a table with the correct number of data rows', () => {
    const rows = fixture.nativeElement.querySelectorAll('tr');
    expect(rows.length).toBe(mockAccountSearchResults.accounts.length + 1); // +1 for the header row
  });

  it('should render table headers correctly', () => {
    const headers = fixture.nativeElement.querySelectorAll('th');
    expect(headers[0]?.textContent).toContain('Target unit name');
    expect(headers[1]?.textContent).toContain('ID');
    expect(headers[2]?.textContent).toContain('Status');
  });

  it('should render the correct data in each row', () => {
    const rows = fixture.nativeElement.querySelectorAll('tr');
    const pipe = new StatusPipe();

    Array.from(rows)
      .slice(1)
      .forEach((row: any, index: number) => {
        const cells = row.querySelectorAll('td');
        const account = mockAccountSearchResults.accounts[index];

        expect(cells[0]?.textContent).toContain(account.name);
        expect(cells[1]?.textContent).toContain(account.businessId);
        expect(cells[2]?.textContent).toContain(pipe.transform(account.status));
      });
  });
});
