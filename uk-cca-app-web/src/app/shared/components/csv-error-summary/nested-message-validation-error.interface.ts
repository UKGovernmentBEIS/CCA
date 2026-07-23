export interface NestedMessageValidationError {
  path: string;
  type: string;
  message?: string;
  columns?: string[];
  rows?: Array<{ rowIndex: number; [key: string]: unknown }>;
  controls?: NestedMessageValidationError[];
}
