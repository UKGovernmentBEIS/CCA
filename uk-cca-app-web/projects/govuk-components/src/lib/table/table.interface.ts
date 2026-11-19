import { GovukTextWidthClass } from '../text-input/text-input.type';

export interface GovukTableColumn<T = any> {
  header: string;
  headerHint?: string;
  field: keyof T;
  widthClass?: GovukTextWidthClass | string;
  isSortable?: boolean;
  isHeader?: boolean;
  /** Right-align the header and cells, including decimal strings and formatted values. */
  isNumeric?: boolean;
}

export interface SortEvent {
  column: GovukTableColumn['field'];
  direction: 'ascending' | 'descending';
}
