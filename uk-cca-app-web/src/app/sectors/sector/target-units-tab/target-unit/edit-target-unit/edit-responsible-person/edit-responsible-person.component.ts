import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ResponsiblePersonFormModel, ResponsiblePersonInputComponent, WizardStepComponent } from '@shared/components';

import { UpdateTargetUnitAccountService } from 'cca-api';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import {
  EDIT_TARGET_UNIT_RESPONSIBLE_PERSON_FORM,
  EditResponsiblePersonFormProvider,
} from './edit-responsible-person-form.provider';

@Component({
  selector: 'cca-edit-responsible-person',
  templateUrl: './edit-responsible-person.component.html',
  imports: [ReactiveFormsModule, WizardStepComponent, RouterLink, ResponsiblePersonInputComponent],
  providers: [EditResponsiblePersonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditResponsiblePersonComponent {
  private readonly updateTargetUnitAccountService = inject(UpdateTargetUnitAccountService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(ActiveTargetUnitStore);

  protected readonly form = inject<FormGroup<ResponsiblePersonFormModel>>(EDIT_TARGET_UNIT_RESPONSIBLE_PERSON_FORM);

  protected readonly accountDetails = this.store.state;

  onSubmit() {
    this.updateTargetUnitAccountService
      .updateTargetUnitAccountResponsiblePerson(+this.route.snapshot.paramMap.get('targetUnitId'), {
        jobTitle: this.form.value.jobTitle,
        phoneNumber: this.form.value.phoneNumber,
      })
      .subscribe(() => {
        this.store.updateResponsiblePerson(this.form.getRawValue());
        this.router.navigate(['../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
