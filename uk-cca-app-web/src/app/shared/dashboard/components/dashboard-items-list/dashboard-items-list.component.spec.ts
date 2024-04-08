import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DashboardItemsListComponent } from '@shared/dashboard';
import { PipesModule } from '@shared/pipes/pipes.module';

import { TableComponent } from 'govuk-components';

import * as mocks from '../../testing';

/* eslint-disable @angular-eslint/component-selector */
@Component({
  selector: '',
  template: `
    <cca-dashboard-items-list
      [items]="items"
      [tableColumns]="tableColumns"
      [unassignedLabel]="'Unassigned'"
    ></cca-dashboard-items-list>
  `,
  standalone: true,
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
      imports: [TestParentComponent, RouterTestingModule, TableComponent, PipesModule],
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
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([...['', 'TEST_FN TEST_LN', '10', 'ACCOUNT_3']]);
  });
});
