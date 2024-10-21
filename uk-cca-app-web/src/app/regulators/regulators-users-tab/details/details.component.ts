import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, Observable } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserState } from '@netz/common/auth';
import { DestroySubject } from '@netz/common/services';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  FieldsetDirective,
  LegendDirective,
  TableComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  FileInputComponent,
  PageHeadingComponent,
  RadioOptionComponent,
  TwoFaLinkComponent,
  UuidFilePair,
} from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { IncludesPipe } from '@shared/pipes';
import { SubmitIfEmptyPipe } from '@shared/pipes/submit-if-empty.pipe';
import { omit } from 'lodash-es';

import { RegulatorUsersService } from 'cca-api';

import { saveNotFoundRegulatorError } from '../../errors/business-error';
import { createForm } from './details.form';
import { DetailsStore } from './details.store';
import { tableColumns, tableRows } from './permissions-table-data';

@Component({
  selector: 'cca-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
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
    RadioOptionComponent,
    PendingButtonDirective,
  ],
  providers: [DestroySubject],
})
export class DetailsComponent {
  private readonly store = inject(DetailsStore);
  private readonly fb = inject(UntypedFormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly regulatorUsersService = inject(RegulatorUsersService);
  private readonly businessErrorService = inject(BusinessErrorService);

  userId = this.route.snapshot.paramMap.get('userId');
  currentUserId = this.authStore.select(selectUserState)().userId;
  isCurrentUser = this.userId === this.currentUserId;

  basePermissionSelected: string;
  userFullName: string;

  isAdd = this.store.state.isAdd;
  isEditable = this.store.state.isEditable;
  userRolePermissions = this.store.state.regulatorRoles;
  permissionGroups = this.store.state.permissionGroupLevels;
  userPermissions = this.store.state.userPermissions;
  user = this.store.state.user;

  isSummaryDisplayed = signal(false);

  form = createForm(this.fb, this.isAdd);

  tableColumns = tableColumns;
  tableRows = tableRows;

  constructor() {
    effect(() => {
      if (this.user) {
        this.form.patchValue({
          user: this.user,
          signature: this.user.signature?.uuid
            ? ({
                uuid: this.user.signature.uuid,
                file: { name: this.user.signature.name } as File,
              } as UuidFilePair)
            : null,
          permissions: this.userPermissions,
        });

        this.form.get('user').get('email').disable();
        this.userFullName = this.user.firstName + ' ' + this.user.lastName;
      }
    });
  }

  setBasePermissions(roleCode: string): void {
    this.basePermissionSelected = roleCode;
    const role = this.userRolePermissions.find((up) => up.code === roleCode);
    const { rolePermissions } = role;

    this.form.get('permissions').patchValue(rolePermissions);
    this.form.markAsDirty();
  }

  submitForm(): void {
    let op$: Observable<unknown>;

    if (this.form.valid) {
      const userEmail = this.form.get('user').get('email').value;
      const signature = this.form.get('signature').value as UuidFilePair;

      if (!signature) {
        this.form.get('signature').setErrors({
          fileNotExist: 'Select a file',
        });

        this.isSummaryDisplayed.set(true);
        return;
      }

      const signatureBlob = signature.file?.size ? signature.file : null;

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
        const payloadWithoutSignature = omit(payload, 'signature');

        op$ = this.regulatorUsersService.inviteRegulatorUserToCA(payloadWithoutSignature, signatureBlob);
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
