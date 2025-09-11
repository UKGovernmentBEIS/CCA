import { FormControl, FormGroup } from '@angular/forms';

import { UuidFilePair } from '@shared/components';

export type DecisionFormModel = FormGroup<{
  type: FormControl<'ACCEPTED' | 'REJECTED'>;
  notes?: FormControl<string>;
  files: FormControl<UuidFilePair[]>;
}>;

export type DecisionFormValue = DecisionFormModel['value'];

export type DecisionWithDateFormModel = FormGroup<{
  type: FormControl<'ACCEPTED' | 'REJECTED'>;
  notes?: FormControl<string>;
  files: FormControl<UuidFilePair[]>;
  changeDate: FormControl<[boolean]>;
  startDate: FormControl<Date>;
}>;

export type DecisionWithDateFormValue = DecisionWithDateFormModel['value'];
