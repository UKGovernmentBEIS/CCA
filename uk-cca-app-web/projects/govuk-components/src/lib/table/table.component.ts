import { NgTemplateOutlet } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  TemplateRef,
  computed,
  input,
  output,
  contentChild,
  inject,
  model,
} from '@angular/core';

import { GovukTableColumn, SortEvent } from './table.interface';

@Component({
  selector: 'govuk-table',
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
  imports: [NgTemplateOutlet],
})
export class TableComponent<T> {
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  readonly columns = input<GovukTableColumn<T>[]>([]);
  readonly data = model<Pick<T, GovukTableColumn<T>['field']>[]>([]);
  readonly caption = input<string>();

  readonly hasHeaderHint = computed(() => this.columns().some((column) => Boolean(column.headerHint)));

  readonly sort = output<SortEvent>();

  readonly template = contentChild(TemplateRef);

  sortedField: GovukTableColumn<T>['field'];
  sortedColumn: GovukTableColumn<T>['header'];
  sortingDirection: 'ascending' | 'descending';

  getTypeof(value: unknown): string {
    return typeof value;
  }

  sortBy(columnField: GovukTableColumn<T>['field']): void {
    this.sortingDirection =
      this.sortedField === columnField && this.sortingDirection === 'ascending' ? 'descending' : 'ascending';
    this.sortedField = columnField;
    this.sortedColumn = this.columns().find((column) => column.field === columnField).header;

    this.changeDetectorRef.markForCheck();

    this.sort.emit({ column: this.sortedField, direction: this.sortingDirection });
  }
}
