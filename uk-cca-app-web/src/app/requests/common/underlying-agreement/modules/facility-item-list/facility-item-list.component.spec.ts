import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { BasePage } from '@netz/common/testing';

import { FacilityItemListComponent } from './facility-item-list.component';

describe('FacilityItemListComponent', () => {
  let component: FacilityItemListComponent;
  let fixture: ComponentFixture<FacilityItemListComponent>;
  let page: Page;

  class Page extends BasePage<FacilityItemListComponent> {
    get facilitiesTable() {
      return this.queryAll<HTMLTableRowElement>('tr')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('td')) ?? []),
          ...(Array.from(row.querySelectorAll('th')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button-group button[type="button"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRoute() }],
    });

    fixture = TestBed.createComponent(FacilityItemListComponent);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('facilityItems', [
      {
        name: 'Factor 1',
        facilityId: 'ADS_1-F00001',
        status: 'NEW',
      },
      {
        name: 'Factor 2',
        facilityId: 'ADS_1-F00002',
        status: 'NEW',
      },
      {
        name: 'Factor 3',
        facilityId: 'ADS_1-F00003',
        status: 'NEW',
      },
    ]);

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show list values', () => {
    expect(page.facilitiesTable).toEqual([
      ['Name', 'Facility ID', 'Status', 'Actions'],
      ['Factor 1', 'ADS_1-F00001', 'New', ''],
      ['Factor 2', 'ADS_1-F00002', 'New', ''],
      ['Factor 3', 'ADS_1-F00003', 'New', ''],
    ]);
  });
});
