import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'patErrorType',
  standalone: true,
})
export class ErrorTypePipe implements PipeTransform {
  transform(errorMessage: string): string {
    if (
      errorMessage === 'CSV_GENERATION_FAILED' ||
      errorMessage === 'REPORT_PACKAGE_MISSING' ||
      errorMessage === 'EXTRACT_VALIDATE_PERSIST_GENERIC_ERROR'
    ) {
      return 'Files could not be uploaded';
    }

    throw new Error('invalid error message');
  }
}
