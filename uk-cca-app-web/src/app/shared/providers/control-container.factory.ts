import { FactoryProvider, Optional } from '@angular/core';
import { ControlContainer, FormGroupDirective, FormGroupName } from '@angular/forms';

export const existingControlContainer: FactoryProvider = {
  provide: ControlContainer,
  useFactory: (name: ControlContainer | null, directive: ControlContainer | null) => name ?? directive,
  deps: [
    [new Optional(), FormGroupName],
    [new Optional(), FormGroupDirective],
  ],
};
