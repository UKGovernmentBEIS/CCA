import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'requestTypeToHeading', pure: true, standalone: true })
export class RequestTypeToHeadingPipe implements PipeTransform {
  transform(value: string): any {
    switch (value) {
      case 'TARGET_UNIT_ACCOUNT_CREATION':
        return 'Account creation';

      case 'UNDERLYING_AGREEMENT':
        return 'Underlying agreement';

      case 'ADMIN_TERMINATION':
        return 'Admin termination';
    }
  }
}
