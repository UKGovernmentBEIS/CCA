import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { UnassignedItemsService } from 'cca-api';

import { UnassignedComponent } from './unassigned.component';

const unassignedItems = {
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
  ],
  totalItems: 1,
};

describe('UnassignedComponent', () => {
  let component: UnassignedComponent;
  let fixture: ComponentFixture<UnassignedComponent>;
  let unassignedItemsService: Partial<jest.Mocked<UnassignedItemsService>>;

  beforeEach(async () => {
    unassignedItemsService = {
      getUnassignedItems: jest.fn().mockReturnValue(of(unassignedItems)),
    };

    await TestBed.configureTestingModule({
      imports: [UnassignedComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: UnassignedItemsService, useValue: unassignedItemsService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UnassignedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render assigned to others table rows', () => {
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
