import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'tprVersion' })
export class TprVersionPipe implements PipeTransform {
  transform(targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9', performanceDataReportVersion: number): string {
    if (!targetPeriodType || !performanceDataReportVersion) throw new Error('Invalid inputs');

    return `${targetPeriodType}-V${performanceDataReportVersion}`;
  }
}
