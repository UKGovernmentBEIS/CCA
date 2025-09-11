import { FormControl } from '@angular/forms';

import { CCAGovukValidators } from '@shared/validators';

export const addSicCodeFormControl = (code?: string): FormControl => {
  return new FormControl(code ?? null, CCAGovukValidators.maxLength('SIC code'));
};
