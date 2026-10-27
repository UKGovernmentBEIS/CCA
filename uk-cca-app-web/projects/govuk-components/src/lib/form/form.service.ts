import { forwardRef, Injectable, Service } from '@angular/core';
import { NgControl } from '@angular/forms';

@Injectable({ providedIn: 'root', useClass: forwardRef(() => DotFormService) })
export abstract class FormService {
  getControlIdentifier(control: NgControl): string {
    return control.path ? this.getIdentifier(control.path) : null;
  }

  abstract getIdentifier(path: string[]): string;
}

@Service({ autoProvided: false })
export class DotFormService extends FormService {
  getIdentifier(path: string[]): string {
    return path.join('.');
  }
}
