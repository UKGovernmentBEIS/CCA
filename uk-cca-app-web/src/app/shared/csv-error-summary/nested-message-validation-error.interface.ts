export interface NestedMessageValidationError {
  path: string;
  type: string;
  message?: string;
  columns?: string[];
  rows?: any[];
  controls?: NestedMessageValidationError[];
}
