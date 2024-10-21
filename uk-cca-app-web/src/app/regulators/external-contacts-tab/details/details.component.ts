import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes, isBadRequest } from '@error/business-errors';
import { ButtonDirective, ErrorSummaryComponent, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { SubmitIfEmptyPipe } from '@shared/pipes/submit-if-empty.pipe';
import { requiredFieldsValidator } from '@shared/validators';

import { CaExternalContactsService } from 'cca-api';

import { saveNotFoundExternalContactError } from '../../errors/business-error';
import { ActiveExternalContactStore } from '../active-external-contact.store';

@Component({
  selector: 'cca-external-contact-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    AsyncPipe,
    ErrorSummaryComponent,
    PageHeadingComponent,
    ReactiveFormsModule,
    TextInputComponent,
    SubmitIfEmptyPipe,
    ButtonDirective,
    PendingButtonDirective,
  ],
})
export class ExternalContactsDetailsComponent {
  private readonly fb = inject(UntypedFormBuilder);
  private readonly caExternalContactsService = inject(CaExternalContactsService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly store = inject(ActiveExternalContactStore);

  isAdd = !!this.store.state;

  form = this.fb.group(
    {
      name: this.fb.control(this.store.state?.name, [
        GovukValidators.required(`Enter the external contact's displayed name`),
        GovukValidators.maxLength(100, `The external contact's displayed name should not be more than 100 characters`),
      ]),
      email: this.fb.control(this.store.state?.email, [
        GovukValidators.required(`Enter external contact's email address`),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
      ]),
      description: this.fb.control(this.store.state?.description, [
        GovukValidators.required(`Enter external contact's description`),
        GovukValidators.maxLength(100, 'Description should not be more than 100 characters'),
      ]),
    },
    { validators: requiredFieldsValidator(), updateOn: 'submit' },
  );

  addExternalContact(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('userId')),
        switchMap((userId) =>
          userId
            ? this.caExternalContactsService.editCaExternalContact(Number(userId), this.form.value)
            : this.caExternalContactsService.createCaExternalContact(this.form.value),
        ),
        catchBadRequest(ErrorCodes.EXTCONTACT1000, () =>
          this.businessErrorService.showError(saveNotFoundExternalContactError),
        ),
      )
      .subscribe({
        next: () => this.router.navigate(['../..'], { relativeTo: this.route, fragment: 'external-contacts' }),
        error: (res: unknown) => this.handleError(res),
      });
  }

  private handleError(res: unknown): void {
    if (isBadRequest(res)) {
      switch (res.error.code) {
        case ErrorCodes.EXTCONTACT1001:
          this.form.get('name').setErrors({ uniqueName: 'Enter a unique displayed name' });
          break;
        case ErrorCodes.EXTCONTACT1002:
          this.form.get('email').setErrors({ uniqueEmail: 'Email address already exists' });
          break;
        case ErrorCodes.EXTCONTACT1003:
          this.form.get('name').setErrors({ uniqueName: 'Enter a unique displayed name' });
          this.form.get('email').setErrors({ uniqueEmail: 'Email address already exists' });
          break;
        default:
          throw res;
      }
    } else {
      throw res;
    }
  }
}
