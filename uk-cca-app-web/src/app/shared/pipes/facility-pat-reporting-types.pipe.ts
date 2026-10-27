import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'facilityPatReportingTypes' })
export class FacilityPatReportingTypesPipe implements PipeTransform {
  transform(value: string): string {
    if (value == null) return '';

    if (typeof value !== 'string') return String(value);

    // Replace underscores with spaces, convert to lower case, then capitalise only the first letter.
    if (!value) return '';

    const sentence = value
      .split('_')
      .filter((part) => part.length > 0)
      .join(' ')
      .toLowerCase();

    return sentence.charAt(0).toUpperCase() + sentence.slice(1);
  }
}
