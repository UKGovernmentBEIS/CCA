import { Pipe, PipeTransform } from '@angular/core';

import { PerformanceDataDownloadSubmitRequestTaskPayload } from 'cca-api';

export enum ErrorMessageTypeEnum {
  GENERATE_ZIP_FAILED = 'Failed to generate main zip file',
  GENERATE_CSV_FAILED = 'Failed to generate error csv file',
  NO_ELIGIBLE_ACCOUNTS_FOR_TPR_REPORTING = 'No eligible accounts found in this sector for the selected target period.',
}

@Pipe({
  name: 'errorMessageType',
  standalone: true,
})
export class ErrorMessageTypePipe implements PipeTransform {
  transform(value: PerformanceDataDownloadSubmitRequestTaskPayload['errorMessage']): string {
    const text = ErrorMessageTypeEnum[value];
    if (!text) throw new Error('invalid error message type');
    return text;
  }
}
