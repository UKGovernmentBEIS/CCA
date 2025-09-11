import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'targetType',
  standalone: true,
})
export class TargetTypePipe implements PipeTransform {
  transform(targetType: 'ABSOLUTE' | 'RELATIVE' | 'NOVEM_ENERGY' | 'NOVEM_CARBON'): string {
    switch (targetType) {
      case 'ABSOLUTE':
        return 'Absolute';

      case 'NOVEM_CARBON':
        return 'Novem Carbon';

      case 'NOVEM_ENERGY':
        return 'Novem energy';

      case 'RELATIVE':
        return 'Relative';

      default:
        throw new Error(`invalid targetType. received ${targetType}`);
    }
  }
}
