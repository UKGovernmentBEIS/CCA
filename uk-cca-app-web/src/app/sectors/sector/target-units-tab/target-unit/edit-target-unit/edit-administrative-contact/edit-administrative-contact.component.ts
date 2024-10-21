import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { LinkDirective } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { UpdateTargetUnitAccountService } from 'cca-api';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { AdministrativeContactInputComponent } from '../../../common/components/administrative-contact-input/administrative-contact-input.component';
import { AdministrativeContactDetailsFormModel } from '../../../common/components/administrative-contact-input/administrative-contact-input-controls';
import {
  EDIT_TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM,
  EditAdministrativeContactFormProvider,
} from './edit-administrative-contact-form.provider';

@Component({
  selector: 'cca-edit-administrative-contact',
  templateUrl: './edit-administrative-contact.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, AdministrativeContactInputComponent, WizardStepComponent, LinkDirective, RouterLink],
  providers: [EditAdministrativeContactFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditAdministrativeContactComponent {
  private readonly updateTargetUnitAccountService = inject(UpdateTargetUnitAccountService);
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(ActiveTargetUnitStore);
  private readonly router = inject(Router);

  readonly form = inject<FormGroup<AdministrativeContactDetailsFormModel>>(
    EDIT_TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM,
  );

  accountDetails = this.store.state;

  onSubmit() {
    this.updateTargetUnitAccountService
      .updateTargetUnitAccountAdministrativePerson(+this.route.snapshot.paramMap.get('targetUnitId'), {
        ...this.form.getRawValue(),
      })
      .subscribe(() => {
        this.store.updateAdministrativeContact(this.form.getRawValue());
        this.router.navigate(['../..'], { relativeTo: this.route });
      });
  }
}
