import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ItemLinkPipe, ItemNamePipe } from '@netz/common/pipes';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TableComponent } from '@netz/govuk-components';

import * as mocks from '../mocks';
import { DashboardItemsListComponent } from './dashboard-items-list.component';

/* eslint-disable @angular-eslint/component-selector */
@Component({
  selector: '',
  template: `
    <cca-dashboard-items-list [items]="items" [tableColumns]="tableColumns" [unassignedLabel]="'Unassigned'" />
  `,
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
    const cells = Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('td'));
    expect(cells.map((cell) => cell.textContent.trim())).toEqual(['', '[object Object]', '10']);
  });
});
