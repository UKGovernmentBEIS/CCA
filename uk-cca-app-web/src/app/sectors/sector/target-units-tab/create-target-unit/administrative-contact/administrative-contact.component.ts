import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { WizardStepComponent } from '@shared/components';

import { AdministrativeContactInputComponent } from '../../common/components/administrative-contact-input/administrative-contact-input.component';
import { AdministrativeContactDetailsFormModel } from '../../common/components/administrative-contact-input/administrative-contact-input-controls';
import { CreateTargetUnitStore } from '../create-target-unit.store';
import {
  TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM,
  TargetUnitAdministrativeContactFormProvider,
} from './administrative-contact-form.provider';

@Component({
  selector: 'cca-administrative-contact',
  templateUrl: './administrative-contact.component.html',
  imports: [ReactiveFormsModule, WizardStepComponent, AdministrativeContactInputComponent],
  providers: [TargetUnitAdministrativeContactFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdministrativeContactComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly createTargetUnitStore = inject(CreateTargetUnitStore);

  protected readonly form = inject<FormGroup<AdministrativeContactDetailsFormModel>>(
    TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM,
  );

  onSubmitAdministrativeContact() {
    const formData = this.form.getRawValue();

    this.createTargetUnitStore.sameAddressWithResponsiblePerson = formData.sameAddress[0];

    delete formData.sameAddress;

    this.createTargetUnitStore.updateState({ administrativeContactDetails: { ...formData } });
    this.router.navigate(['..', 'summary'], { relativeTo: this.activatedRoute });
  }
}
