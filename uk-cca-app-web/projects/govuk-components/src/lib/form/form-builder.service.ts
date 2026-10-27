import { Service } from '@angular/core';
import { AbstractControlOptions, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

@Service({ autoProvided: false })
export class FormBuilderService extends UntypedFormBuilder {
  override group(controlsConfig: Record<string, any>, options?: AbstractControlOptions | null): UntypedFormGroup {
    return super.group(controlsConfig, { updateOn: 'submit', ...options });
  }
}
