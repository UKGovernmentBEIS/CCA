import { GovukTextWidthClass } from '../text-input/text-input.type';

export interface SelectableRow {
  isSelectable: boolean;
}

export interface CcaTableColumn {
  header: string;
  field: string;
  widthClass?: GovukTextWidthClass | string;
  isSortable?: boolean;
  isHeader?: boolean;
  primary?: boolean;
}

export interface SortEvent {
  column: CcaTableColumn['field'];
  direction: 'ascending' | 'descending';
}
