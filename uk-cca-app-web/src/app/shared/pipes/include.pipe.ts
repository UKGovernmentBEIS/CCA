import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'includes', standalone: true })
export class IncludesPipe implements PipeTransform {
  transform<T, R extends { includes: (value: T) => boolean }>(value: R | null, target: T): boolean {
    return value && value.includes(target);
  }
}
