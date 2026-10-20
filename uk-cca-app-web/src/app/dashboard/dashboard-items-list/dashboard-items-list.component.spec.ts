import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ItemLinkPipe, ItemNamePipe } from '@netz/common/pipes';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TableComponent } from '@netz/govuk-components';
import { getAllByRole, within } from '@testing';

import * as mocks from '../mocks';
import { DashboardItemsListComponent } from './dashboard-items-list.component';

/* eslint-disable @angular-eslint/component-selector */
@Component({
  selector: '',
  template: ` <cca-dashboard-items-list [items]="items" [tableColumns]="tableColumns" /> `,
  imports: [DashboardItemsListComponent],
})
class TestParentComponent {
  items = mocks.assignedItems;
  tableColumns = mocks.columns;
}

describe('WorkflowItemsListComponent', () => {
  let component: TestParentComponent;
  let fixture: ComponentFixture<TestParentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestParentComponent, TableComponent, ItemLinkPipe, ItemNamePipe],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(TestParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show data in table', () => {
    const cells = getAllByRole('row', {}, fixture.nativeElement)
      .slice(1)
      .flatMap((row) => within(row).getAllByRole('cell'));
    const cellText = cells.map((cell) => cell.textContent.replace(/\s+/g, ' ').trim());

    expect(cellText).toEqual([
      'Review application for underlying agreement New',
      'TU-001',
      'Acme Manufacturing Ltd',
      'F-001 - Leeds Works',
      '10',
      'FBS',
      'TP reporting (TP6) - Upload spreadsheets',
      'TU-002',
      'North Energy Ltd',
      'F-002 - Cardiff Site',
      '',
      'CHEM',
    ]);
  });
});
