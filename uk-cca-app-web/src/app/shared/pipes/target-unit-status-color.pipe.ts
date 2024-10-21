import { Pipe, PipeTransform } from '@angular/core';

import { TagColor } from '@netz/govuk-components';

@Pipe({
  name: 'statusColorPipe',
  standalone: true,
})
export class TargetUnitStatusColorPipe implements PipeTransform {
  transform(status: string): TagColor {
    const lowerCaseStatus = status.toLowerCase();
    if (lowerCaseStatus === 'live') return 'green';
    if (lowerCaseStatus === 'new') return 'grey';
    return 'red';
  }
}
