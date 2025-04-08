import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';

import { map, Observable, of } from 'rxjs';

import { FacilityService } from 'cca-api';

export function facilityExistenceValidator(facilityService: FacilityService): AsyncValidatorFn {
  return (group: AbstractControl): Observable<ValidationErrors | null> => {
    const facilityId = group.value;

    if (facilityId) {
      return facilityService
        .isActiveFacility(facilityId)
        .pipe(
          map((exists) => (exists ? null : { facilityIdNotExists: 'Enter the facility ID of an existing facility' })),
        );
    }

    return of(null);
  };
}
