import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, UntypedFormArray } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { UserFullNamePipe } from '@netz/common/pipes';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
} from '@netz/govuk-components';
import { UsersTableDirective } from '@shared/directives';

import { RegulatorAuthoritiesService, RegulatorUsersAuthoritiesInfoDTO } from 'cca-api';

import { savePartiallyNotFoundRegulatorError } from '../errors/business-error';

@Component({
  selector: 'cca-regulators-users',
  templateUrl: './regulators-users.component.html',
  imports: [
    ReactiveFormsModule,
    TableComponent,
    UsersTableDirective,
    RouterLink,
    UserFullNamePipe,
    ErrorSummaryComponent,
    SelectComponent,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorsUsersComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authStore = inject(AuthStore);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly businessErrorService = inject(BusinessErrorService);

  private readonly regulatorsData: WritableSignal<RegulatorUsersAuthoritiesInfoDTO | null> = signal(null);
  protected readonly regulators = computed(() => this.regulatorsData()?.caUsers || []);
  protected readonly isEditable = computed(() => this.regulatorsData()?.editable);

  protected readonly regulators$ = toObservable(this.regulators);

  protected readonly isErrorSummaryDisplayed = signal(false);
  protected readonly userId = this.authStore.select(selectUserId);
  protected readonly regulatorsForm = this.fb.group({ regulatorsArray: this.fb.array([]) });

  protected readonly authorityStatuses: GovukSelectOption<string>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];

  protected readonly authorityStatusesAccepted: GovukSelectOption<string>[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];

  protected readonly editableCols: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'jobTitle', header: 'Job title' },
    { field: 'authorityStatus', header: 'Account status' },
    { field: 'deleteBtn', header: 'Actions' },
  ];

  protected readonly nonEditableCols: GovukTableColumn[] = this.editableCols.slice(0, 2);

  get regulatorsArray(): UntypedFormArray {
    return this.regulatorsForm.get('regulatorsArray') as UntypedFormArray;
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.regulatorAuthoritiesService.getCaRegulators().subscribe((r) => this.regulatorsData.set(r));
  }

  saveRegulators() {
    if (!this.regulatorsForm.dirty) return;

    if (!this.regulatorsForm.valid) {
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.regulatorAuthoritiesService
        .updateCompetentAuthorityRegulatorUsersStatus(
          this.regulatorsArray.controls
            .filter((control) => control.dirty)
            .map((control) => ({
              authorityStatus: control.value.authorityStatus,
              userId: control.value.userId,
            })),
        )
        .pipe(
          catchBadRequest(ErrorCodes.AUTHORITY1003, () =>
            this.businessErrorService.showError(savePartiallyNotFoundRegulatorError),
          ),
          tap(() => this.regulatorsForm.markAsPristine()),
        )
        .subscribe(() => this.refresh());
    }
  }
}
