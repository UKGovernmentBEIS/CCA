import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, Observable } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserState } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  FieldsetDirective,
  LegendDirective,
  RadioComponent,
  RadioOptionComponent as GovukRadioOptionComponent,
  TableComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { FileInputComponent, RadioOptionComponent, TwoFaLinkComponent } from '@shared/components';
import { IncludesPipe, SubmitIfEmptyPipe } from '@shared/pipes';
import { omit } from '@shared/utils';

import { RegulatorUsersService } from 'cca-api';

import { saveNotFoundRegulatorError } from '../../errors/business-error';
import { createForm } from './details.form';
import { DetailsStore } from './details.store';
import { tableColumns, tableRows } from './permissions-table-data';

@Component({
  selector: 'cca-details',
  templateUrl: './details.component.html',
  imports: [
    ErrorSummaryComponent,
    ReactiveFormsModule,
    PageHeadingComponent,
    FileInputComponent,
    TableComponent,
    IncludesPipe,
    FieldsetDirective,
    LegendDirective,
    TextInputComponent,
    TwoFaLinkComponent,
    ButtonDirective,
    SubmitIfEmptyPipe,
    RadioComponent,
    GovukRadioOptionComponent,
    RadioOptionComponent,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  private readonly store = inject(DetailsStore);
  private readonly fb = inject(UntypedFormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly regulatorUsersService = inject(RegulatorUsersService);
  private readonly businessErrorService = inject(BusinessErrorService);

  protected readonly userId = this.route.snapshot.paramMap.get('userId');
  protected readonly currentUserId = this.authStore.select(selectUserState)().userId;
  protected readonly isCurrentUser = this.userId === this.currentUserId;

  protected readonly isAdd = this.store.state.isAdd;
  protected readonly isEditable = this.store.state.isEditable;
  protected readonly userRolePermissions = this.store.state.regulatorRoles;
  protected readonly permissionGroups = this.store.state.permissionGroupLevels;
  protected readonly userPermissions = this.store.state.userPermissions;
  protected readonly user = this.store.state.user;

  protected readonly isSummaryDisplayed = signal(false);

  protected readonly form = createForm(this.fb, this.isAdd, this.user, this.userPermissions);

  // The radio group needs a control with a form path: a control that is bound on its own has none, so the
  // options would end up with an empty name and would not be grouped. This group exists only to give the
  // control a name; it is not part of the submitted form.
  protected readonly basePermissionsForm = this.fb.group({ basePermission: '' });
  protected readonly userFullName = `${this.user?.firstName}' '${this.user?.lastName}`;

  protected readonly tableColumns = tableColumns;
  protected readonly tableRows = tableRows;

  private readonly selectedBasePermission = toSignal(this.basePermissionsForm.controls.basePermission.valueChanges, {
    initialValue: this.basePermissionsForm.controls.basePermission.value,
  });

  private readonly permissions = toSignal(this.form.controls.permissions.valueChanges, {
    initialValue: this.form.controls.permissions.value,
  });

  // The permissions the last applied base role wrote. Any other change to the permissions is an edit by
  // hand, and it invalidates the role selection.
  private readonly appliedPermissions = signal<Partial<Record<string, string>> | null>(null);

  private readonly permissionsEditedByHand = computed(() => {
    const appliedPermissions = this.appliedPermissions();

    return appliedPermissions !== null && !samePermissions(appliedPermissions, this.permissions());
  });

  constructor() {
    // Choosing a base role applies its permission defaults.
    effect(() => {
      const roleCode = this.selectedBasePermission();

      if (roleCode) {
        this.setBasePermissions(roleCode);
      }
    });

    // Editing a permission means the selected role no longer describes the form, so the radio is cleared:
    // choosing that role again is then a value change, and it re-applies its defaults. The clear has to
    // emit, otherwise the selection signal keeps the role and re-choosing it would look unchanged.
    effect(() => {
      if (this.permissionsEditedByHand()) {
        this.basePermissionsForm.controls.basePermission.setValue('');
      }
    });
  }

  private setBasePermissions(roleCode: string): void {
    const role = this.userRolePermissions.find((up) => up.code === roleCode);

    if (!role) {
      // Role codes come from the API, the options are fixed: drop the selection rather than claim that a
      // role the service does not know about was applied.
      this.basePermissionsForm.controls.basePermission.setValue('');
      return;
    }

    this.form.controls.permissions.patchValue(role.rolePermissions);
    // The same projection as the value the changes are read from, so an untouched key cannot look edited.
    this.appliedPermissions.set(this.form.controls.permissions.value);

    this.form.markAsDirty();
  }

  submitForm(): void {
    let op$: Observable<unknown>;

    if (this.form.valid) {
      const userEmail = this.form.get('user').get('email').value;
      const signature = this.form.get('signature').value;

      if (!signature) {
        this.form.get('signature').setErrors({
          fileNotExist: 'Select a file',
        });

        this.isSummaryDisplayed.set(true);
        return;
      }

      const signatureBlob = signature.file instanceof File ? signature.file : undefined;

      if (!this.isAdd) {
        const payload = { ...this.form.getRawValue() };
        const payloadWithoutSignature = omit(payload, 'signature');

        op$ = this.isCurrentUser
          ? this.regulatorUsersService.updateCurrentRegulatorUser(payloadWithoutSignature, signatureBlob)
          : this.regulatorUsersService.updateRegulatorUserByCaAndId(
              this.userId,
              payloadWithoutSignature,
              signatureBlob,
            );
      } else {
        const payload = { ...this.form.get('user').value, permissions: this.form.get('permissions').value };

        op$ = this.regulatorUsersService.inviteRegulatorUserToCA(payload, signatureBlob);
      }

      op$
        .pipe(
          catchBadRequest([ErrorCodes.USER1001, ErrorCodes.AUTHORITY1005, ErrorCodes.AUTHORITY1014], () => {
            this.form.get('user').get('email').setErrors({
              emailExists: 'This user email already exists in the service',
            });

            this.isSummaryDisplayed.set(true);
            return EMPTY;
          }),
          catchBadRequest(ErrorCodes.AUTHORITY1003, () =>
            this.businessErrorService.showError(saveNotFoundRegulatorError),
          ),
        )
        .subscribe(() =>
          this.isAdd
            ? this.router.navigate(['../../regulators', 'add-confirmation'], {
                relativeTo: this.route,
                queryParams: { email: userEmail },
                replaceUrl: true,
              })
            : this.router.navigate(['../../regulators'], { relativeTo: this.route }),
        );
    } else {
      this.isSummaryDisplayed.set(true);
    }
  }

  getCurrentUserDownloadUrl(uuid: string): string | string[] {
    return ['..', 'file-download', uuid];
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['file-download', uuid];
  }
}

function samePermissions(left: Partial<Record<string, string>>, right: Partial<Record<string, string>>): boolean {
  const keys = Object.keys(left);

  return keys.length === Object.keys(right).length && keys.every((key) => left[key] === right[key]);
}
