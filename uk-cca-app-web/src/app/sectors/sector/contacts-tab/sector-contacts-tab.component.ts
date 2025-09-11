import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EMPTY, startWith } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { UserFullNamePipe } from '@netz/common/pipes';
import {
  ButtonDirective,
  DetailsComponent,
  ErrorSummaryComponent,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  SortEvent,
  TableComponent,
} from '@netz/govuk-components';

import {
  SectorAssociationAuthoritiesService,
  SectorUserAuthorityInfoDTO,
  SectorUserAuthorityUpdateDTO,
  SectorUsersAuthoritiesInfoDTO,
} from 'cca-api';

import {
  hasAdministrator,
  isTheOnlyAdministrator,
  patchAuthoritiesForm,
  SectorAuthorityFormModel,
  setAdministratorFormError,
} from './sector-authorities-form.utils';

@Component({
  selector: 'cca-sector-contacts-tab',
  templateUrl: './sector-contacts-tab.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DetailsComponent,
    SelectComponent,
    ErrorSummaryComponent,
    TableComponent,
    RouterLink,
    ButtonDirective,
    UserFullNamePipe,
    TitleCasePipe,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorContactsTabComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authStore = inject(AuthStore);
  private readonly route = inject(ActivatedRoute);
  private readonly sectorAssociationAuthoritiesService = inject(SectorAssociationAuthoritiesService);
  private readonly router = inject(Router);

  private readonly sectorId = +this.route.snapshot.paramMap.get('sectorId');
  private readonly sectorUsersAuthorities = signal<SectorUsersAuthoritiesInfoDTO | null>(null);

  protected readonly isEditable = computed(() => this.sectorUsersAuthorities()?.editable);
  protected readonly userId = this.authStore.select(selectUserId);

  protected readonly authorities = computed(() => {
    const sorting = this.sorting();
    const authorities: SectorUserAuthorityInfoDTO[] = this.sectorUsersAuthorities()?.authorities;

    if (!sorting) return authorities;

    return authorities.sort((a, b) => {
      const diff = a.firstName.localeCompare(b.firstName, 'en-GB', { numeric: true, sensitivity: 'base' });
      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    });
  });

  private readonly sorting = signal<SortEvent | null>(null);

  protected readonly createUserTypeOptions: GovukSelectOption<string>[] = [
    { text: 'Administrator user', value: 'sector_user_administrator' },
    { text: 'Basic user', value: 'sector_user_basic_user' },
  ];

  protected readonly authorityStatuses: GovukSelectOption<string>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];

  protected readonly authorityStatusesAccepted: GovukSelectOption<string>[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];

  protected readonly sectorContactUsersColumns: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'userType', header: 'User type' },
    { field: 'contactType', header: 'Contact type' },
    { field: 'status', header: 'Account status' },
    { field: 'deleteBtn', header: 'Actions' },
  ];

  protected readonly nonEditableCols: GovukTableColumn[] = this.sectorContactUsersColumns.slice(0, -1);

  protected readonly createUserForm = this.fb.group({ userType: this.createUserTypeOptions[0].value });
  protected readonly authoritiesForm = this.fb.group({ authorities: this.fb.array<SectorAuthorityFormModel>([]) });

  protected readonly status = toSignal(this.authoritiesForm.statusChanges.pipe(startWith(this.authoritiesForm.status)));

  get authoritiesArray() {
    return this.authoritiesForm.controls.authorities;
  }

  ngOnInit(): void {
    this.refresh();
  }

  sortBy(sorting: SortEvent) {
    this.sorting.set(sorting);
  }

  refresh() {
    this.fetchSectorUsersAuthorities().subscribe((usersAuthorities) => {
      this.sectorUsersAuthorities.set(usersAuthorities);
      patchAuthoritiesForm(usersAuthorities.authorities, this.authoritiesForm, this.fb);
    });
  }

  onSelectUserType() {
    if (!this.createUserForm.valid) return;

    const role = this.createUserForm.value.userType;
    this.router.navigate(['sector-user', 'add'], { queryParams: { role }, relativeTo: this.route });
  }

  onDeleteSectorUser(userId: string) {
    this.authoritiesForm.markAsTouched();

    isTheOnlyAdministrator(userId, this.sectorUsersAuthorities().authorities)
      ? setAdministratorFormError(this.authoritiesForm)
      : this.router.navigate(['sector-user', userId, 'delete'], { relativeTo: this.route });
  }

  onSaveAuthorities() {
    this.authoritiesForm.markAsTouched();
    if (this.authoritiesForm.pristine) return;

    if (!hasAdministrator(this.authoritiesForm)) {
      return this.authoritiesForm.setErrors({
        atLeastOneAdmin: 'At least one sector admin should exist in sector association',
      });
    }

    this.sectorAssociationAuthoritiesService
      .updateSectorUserAuthorities(this.sectorId, {
        sectorUserAuthorityUpdateDTOList: this.authoritiesForm
          .getRawValue()
          .authorities.map(({ status, userId, userType }) => ({
            roleCode: userType,
            userId,
            authorityStatus: status as SectorUserAuthorityUpdateDTO['authorityStatus'],
          })),
      })
      .pipe(
        catchBadRequest(ErrorCodes.CCAAUTHORITY1002, () => {
          this.authoritiesForm.setErrors({
            emailExists: 'At least one sector admin should exist in sector association',
          });
          return EMPTY;
        }),
      )
      .subscribe();
  }

  private fetchSectorUsersAuthorities() {
    return this.sectorAssociationAuthoritiesService.getSectorUserAuthoritiesBySectorAssociationId(this.sectorId);
  }
}
