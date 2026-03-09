import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, ParamMap } from '@angular/router';

import { of, Subject } from 'rxjs';

import { CcaItemDTO, ItemDTOResponse } from 'cca-api';

import { DashboardFetchFn, DashboardTaskListComponent } from './dashboard-task-list.component';

class ActivatedRouteTestStub {
  private readonly queryParamSubject = new Subject<ParamMap>();
  readonly queryParamMap = this.queryParamSubject.asObservable();
  readonly snapshot = { fragment: null } as any;

  emitQueryParams(params?: Record<string, string | number>) {
    this.queryParamSubject.next(convertToParamMap(params ?? {}));
  }
}

@Component({
  selector: 'cca-dashboard-task-list-host',
  template: `<cca-dashboard-task-list heading="Test" [fetchFn]="fetchFn" />`,
  imports: [DashboardTaskListComponent],
  standalone: true,
})
class DashboardTaskListHostComponent {
  fetchFn: DashboardFetchFn = () => of({ items: [], totalItems: 0 });
}

describe('DashboardTaskListComponent', () => {
  let fixture: ComponentFixture<DashboardTaskListHostComponent>;
  let routeStub: ActivatedRouteTestStub;

  beforeEach(async () => {
    routeStub = new ActivatedRouteTestStub();

    await TestBed.configureTestingModule({
      imports: [DashboardTaskListHostComponent],
      providers: [{ provide: ActivatedRoute, useValue: routeStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardTaskListHostComponent);
  });

  it('should render assigned to me items', () => {
    const mockTasks: ItemDTOResponse = {
      items: [
        {
          taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
          requestType: 'UNDERLYING_AGREEMENT',
          taskAssigneeType: 'REGULATOR',
          daysRemaining: null,
          taskAssignee: null,
          creationDate: new Date('2020-11-13T13:00:00Z').toISOString(),
          requestId: '1',
          taskId: 2,
          isNew: true,
          accountName: 'DUMMY_ACCOUNT_NAME',
          businessId: 'DUMMY_BUSINESS_ID',
          sectorAcronym: 'ADS_1',
          sectorName: 'Sector 1',
          facilityBusinessId: 'ADS_1-F00001',
          siteName: 'fac1-1',
        },
        {
          taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
          requestType: 'UNDERLYING_AGREEMENT',
          taskAssigneeType: 'OPERATOR',
          daysRemaining: 13,
          taskAssignee: { firstName: 'Sasha', lastName: 'Baron Cohen' },
          creationDate: new Date('2020-11-13T15:00:00Z').toISOString(),
          requestId: '3',
          taskId: 4,
          isNew: false,
          accountName: 'DUMMY_ACCOUNT_NAME2',
          businessId: 'DUMMY_BUSINESS_ID2',
          sectorAcronym: 'ADS_2',
          sectorName: 'Sector 2',
          facilityBusinessId: 'ADS_1-F00002',
          siteName: 'fac1-2',
        },
      ] as CcaItemDTO[],
      totalItems: 2,
    };

    fixture.componentInstance.fetchFn = () => of(mockTasks);
    fixture.detectChanges();
    routeStub.emitQueryParams({});
    fixture.detectChanges();

    const cells = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[]);
    const anchors = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[])
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);

    expect(anchors.map((anchor) => anchor.href).length).toEqual(2);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      'New',
      'DUMMY_BUSINESS_ID',
      'DUMMY_ACCOUNT_NAME',
      'ADS_1-F00001 - fac1-1',
      '',
      'ADS_1',
      '',
      'DUMMY_BUSINESS_ID2',
      'DUMMY_ACCOUNT_NAME2',
      'ADS_1-F00002 - fac1-2',
      '13',
      'ADS_2',
    ]);
  });

  it('should render assigned to others items', () => {
    const mockTasks: ItemDTOResponse = {
      items: [
        {
          taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
          requestType: 'UNDERLYING_AGREEMENT',
          taskAssigneeType: 'REGULATOR',
          daysRemaining: 13,
          taskAssignee: { firstName: 'Sasha', lastName: 'Baron Cohen' },
          creationDate: new Date('2020-11-13T15:00:00Z').toISOString(),
          requestId: '3',
          taskId: 4,
          isNew: false,
          accountName: 'DUMMY_ACCOUNT_NAME2',
          businessId: 'DUMMY_BUSINESS_ID2',
          sectorAcronym: 'ADS_2',
          sectorName: 'Sector 2',
          facilityBusinessId: 'ADS_1-F00002',
          siteName: 'fac1-2',
        },
      ] as CcaItemDTO[],
      totalItems: 1,
    };

    fixture.componentInstance.fetchFn = () => of(mockTasks);
    fixture.detectChanges();
    routeStub.emitQueryParams({});
    fixture.detectChanges();

    const cells = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[]);
    const anchors = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[])
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);

    expect(anchors.map((anchor) => anchor.href).length).toEqual(1);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      '',
      'DUMMY_BUSINESS_ID2',
      'DUMMY_ACCOUNT_NAME2',
      'ADS_1-F00002 - fac1-2',
      '13',
      'ADS_2',
    ]);
  });

  it('should render unassigned items', () => {
    const mockTasks: ItemDTOResponse = {
      items: [
        {
          taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
          requestType: 'UNDERLYING_AGREEMENT',
          taskAssigneeType: 'REGULATOR',
          creationDate: new Date('2020-11-27T10:13:49Z').toISOString(),
          requestId: '40',
          taskId: 19,
          daysRemaining: 3,
          accountName: 'DUMMY_ACCOUNT_NAME3',
          businessId: 'DUMMY_BUSINESS_ID3',
          sectorAcronym: 'ADS_3',
          sectorName: 'Sector 3',
          facilityBusinessId: 'ADS_1-F00003',
          siteName: 'fac1-3',
        },
      ] as CcaItemDTO[],
      totalItems: 1,
    };

    fixture.componentInstance.fetchFn = () => of(mockTasks);
    fixture.detectChanges();
    routeStub.emitQueryParams({});
    fixture.detectChanges();

    const cells = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[]);
    const anchors = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[])
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);

    expect(anchors.map((anchor) => anchor.href).length).toEqual(1);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      '',
      'DUMMY_BUSINESS_ID3',
      'DUMMY_ACCOUNT_NAME3',
      'ADS_1-F00003 - fac1-3',
      '3',
      'ADS_3',
    ]);
  });
});
