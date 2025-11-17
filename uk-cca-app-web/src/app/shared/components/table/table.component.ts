import { NgClass, NgTemplateOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  computed,
  contentChild,
  inject,
  input,
  output,
  signal,
  TemplateRef,
} from '@angular/core';

import { SortEvent } from '@netz/govuk-components';

import { CcaTableColumn, SelectableRow } from './types';

@Component({
  selector: 'cca-table',
  templateUrl: './table.component.html',
  styles: `
    .checkbox-item {
      width: 50px;
      vertical-align: top;
      padding: 0 0 0 10px;
    }
  `,
  imports: [NgTemplateOutlet, NgClass],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableComponent {
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  protected readonly columns = input.required<CcaTableColumn[]>();
  protected readonly data = input.required<any[]>();
  protected readonly caption = input<string>();
  protected readonly selectedRows = input(new Map());
  protected readonly showSelectAllCheckbox = input<boolean>();

  readonly rowSelectionChange = output<{ row: unknown; checked: boolean }>();
  readonly allRowsSelectionChange = output<boolean>();
  readonly sort = output<SortEvent>();

  protected readonly template = contentChild(
    TemplateRef<{
      column: CcaTableColumn;
      row: CcaTableColumn['field'] & SelectableRow;
      index: number;
    }>,
  );

  protected readonly sortingDirection = signal<'ascending' | 'descending'>('ascending');
  protected readonly sortedField = signal(null);
  protected readonly sortedColumn = signal(null);

  protected readonly primaryColumn = computed(() => this.columns().find((column) => column.primary).field);

  getTypeof(value: unknown): string {
    return typeof value;
  }

  sortBy(columnField: unknown): void {
    this.sortingDirection.set(
      this.sortedField() === columnField && this.sortingDirection() === 'ascending' ? 'descending' : 'ascending',
    );

    this.sortedField.set(columnField);
    this.sortedColumn.set(this.columns().find((column) => column.field === columnField).header);

    this.changeDetectorRef.markForCheck();

    this.sort.emit({ column: this.sortedField(), direction: this.sortingDirection() });
  }

  areAllSelectableRowsSelected(): boolean {
    const selectableRows = this.data().filter((row) => row.isSelectable);
    return selectableRows.every((r) => this.selectedRows().has(r[this.primaryColumn()]));
  }

  onRowSelectionToggle(row: any, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    this.rowSelectionChange.emit({ row, checked });
  }

  onSelectAllToggle(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    this.allRowsSelectionChange.emit(checked);
  }

  isRowSelected(row: unknown): boolean {
    return this.selectedRows().has(row[this.primaryColumn()]);
  }
}
