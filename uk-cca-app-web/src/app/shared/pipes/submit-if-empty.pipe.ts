import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'submitIfEmpty' })
export class SubmitIfEmptyPipe implements PipeTransform {
  transform(value: unknown): string {
    return value ? 'Save' : 'Submit';
  }
}
