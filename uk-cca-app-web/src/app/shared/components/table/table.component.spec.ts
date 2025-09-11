import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { TableComponent } from './table.component';
import { CcaTableColumn } from './types';

describe('TableComponent', () => {
  let component: TableComponent;
  let componentRef: ComponentRef<TableComponent>;
  let fixture: ComponentFixture<TableComponent>;

  const testColumns: CcaTableColumn[] = [
    { header: 'Name', field: 'name', primary: true },
    { header: 'Age', field: 'age' },
    { header: 'Email', field: 'email' },
  ];

  const testData = [
    { name: 'John Doe', age: 30, email: 'john@example.com', isSelectable: true },
    { name: 'Jane Smith', age: 25, email: 'jane@example.com', isSelectable: true },
    { name: 'Bob Johnson', age: 40, email: 'bob@example.com', isSelectable: false },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TableComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;

    componentRef.setInput('columns', testColumns);
    componentRef.setInput('data', testData);
    componentRef.setInput('selectedRows', new Map());
    componentRef.setInput('showSelectAllCheckbox', true);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Checkbox visibility', () => {
    it('should display checkboxes in selectable rows', () => {
      const checkboxes = fixture.debugElement.queryAll(By.css('tbody input[type="checkbox"]'));
      expect(checkboxes.length).toBe(2); // Only selectable rows should have checkboxes
    });

    it('should not display checkbox for row with isSelectable=false', () => {
      const rows = fixture.debugElement.queryAll(By.css('tbody tr'));
      expect(rows.length).toBe(3);

      const thirdRowCheckbox = rows[2].query(By.css('input[type="checkbox"]'));
      expect(thirdRowCheckbox).toBeNull();
    });
  });

  describe('Row selection', () => {
    it('should emit selection change when row checkbox is clicked', () => {
      const firstRowCheckbox = fixture.debugElement
        .queryAll(By.css('tbody tr'))[0]
        .query(By.css('input[type="checkbox"]')).nativeElement;

      const selectChangeSpy = jest.spyOn(component.rowSelectionChange, 'emit');

      firstRowCheckbox.click();
      fixture.detectChanges();

      expect(selectChangeSpy).toHaveBeenCalledWith({
        row: testData[0],
        checked: true,
      });
    });

    it('should reflect selection state from selected input', () => {
      const selectedMap = new Map([[testData[0].name, testData[0]]]);
      componentRef.setInput('selectedRows', selectedMap);
      fixture.detectChanges();

      expect(component.isRowSelected(testData[0])).toBeTruthy();
      expect(component.isRowSelected(testData[1])).toBeFalsy();
    });
  });

  describe('Select all functionality', () => {
    it('should emit selectAllChange when header checkbox is clicked', () => {
      const headerCheckbox = fixture.debugElement.query(By.css('thead input[type="checkbox"]'));
      expect(headerCheckbox).toBeTruthy();

      const selectAllChangeSpy = jest.spyOn(component.allRowsSelectionChange, 'emit');

      headerCheckbox.nativeElement.click();
      fixture.detectChanges();

      expect(selectAllChangeSpy).toHaveBeenCalledWith(true);
    });

    it('should show header checkbox only when there are selectable rows', () => {
      const nonSelectableData = [
        { name: 'John', age: 30, isSelectable: false },
        { name: 'Jane', age: 25, isSelectable: false },
      ];

      componentRef.setInput('data', nonSelectableData);
      componentRef.setInput('showSelectAllCheckbox', false);
      fixture.detectChanges();

      const headerCheckbox = fixture.debugElement.query(By.css('thead input[type="checkbox"]'));
      expect(headerCheckbox).toBeNull();
    });
  });

  describe('Sorting', () => {
    it('should emit sort event when sortable column header is clicked', () => {
      const sortableColumns: CcaTableColumn[] = [
        { header: 'Name', field: 'name', primary: true, isSortable: true },
        { header: 'Age', field: 'age' },
        { header: 'Email', field: 'email' },
      ];

      componentRef.setInput('columns', sortableColumns);
      fixture.detectChanges();

      const sortSpy = jest.spyOn(component.sort, 'emit');

      const sortButton = fixture.debugElement.query(By.css('th button'));
      sortButton.nativeElement.click();
      fixture.detectChanges();

      expect(sortSpy).toHaveBeenCalledWith({
        column: 'name',
        direction: 'ascending',
      });

      // Click again to test descending
      sortButton.nativeElement.click();
      fixture.detectChanges();

      expect(sortSpy).toHaveBeenCalledWith({
        column: 'name',
        direction: 'descending',
      });
    });
  });
});
