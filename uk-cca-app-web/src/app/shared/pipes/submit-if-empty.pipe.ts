import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'submitIfEmpty',
  standalone: true,
})
export class SubmitIfEmptyPipe implements PipeTransform {
  transform(value: any): string {
    return value ? 'Save' : 'Submit';
  }
}
