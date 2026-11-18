import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserId, selectUserRoleType } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { UserFullNamePipe } from '@netz/common/pipes';
import {
  ButtonDirective,
  DetailsComponent,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  SortEvent,
  TableComponent,
} from '@netz/govuk-components';

import {
  AccountOperatorAuthorityUpdateDTO,
  OperatorAuthoritiesService,
  OperatorAuthorityInfoDTO,
  SectorUserAuthorityInfoDTO,
} from 'cca-api';

export type TargetUnitUserFormModel = FormGroup<{
  userType: FormControl<string>;
  status: FormControl;
  userId: FormControl<string>;
}>;

@Component({
  selector: 'cca-users-and-contacts',
  templateUrl: './users-and-contacts-tab.component.html',
  imports: [
    ButtonDirective,
    RouterLink,
    DetailsComponent,
    ReactiveFormsModule,
    TableComponent,
    TitleCasePipe,
    UserFullNamePipe,
    SelectComponent,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsersAndContactsTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly authStore = inject(AuthStore);
  private readonly operatorAuthorities = inject(OperatorAuthoritiesService);

  private _users = signal<SectorUserAuthorityInfoDTO[]>([]);

  protected readonly sorting = signal<SortEvent | null>(null);
  protected readonly isEditable = signal(false);

  protected readonly currentUserId = this.authStore.select(selectUserId);
  protected readonly roleType = this.authStore.select(selectUserRoleType);

  protected readonly usersForm = this.fb.group({ users: this.fb.array<TargetUnitUserFormModel>([]) });
  protected readonly targetUnitId = this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  protected readonly users = computed(() => {
    const sorting = this.sorting();
    const users: SectorUserAuthorityInfoDTO[] = this._users();

    if (!sorting) return users;

    return users.sort((a, b) => {
      const diff = a.firstName.localeCompare(b.firstName);
      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    });
  });

  protected readonly targetUnitUsersColumns: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'roleName', header: 'User type' },
    { field: 'contactType', header: 'Contact type' },
    { field: 'status', header: 'Account status' },
    { field: 'deleteBtn', header: 'Actions' },
  ];

  protected readonly authorityStatuses: GovukSelectOption[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];

  protected readonly authorityStatusesAccepted: GovukSelectOption[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];

  get usersFormArray() {
    return this.usersForm.controls.users;
  }

  constructor() {
    this.refresh();
  }

  refresh() {
    this.operatorAuthorities.getAccountOperatorAuthorities(+this.targetUnitId).subscribe((r) => {
      this._users.set(r.authorities);
      this.isEditable.set(r.editable);
      this.patchUsersForm(r.authorities);
    });
  }

  sortBy(sorting: SortEvent) {
    this.sorting.set(sorting);
  }

  onSave() {
    const payload: AccountOperatorAuthorityUpdateDTO[] = this.usersFormArray.value.map((u) => ({
      userId: u.userId,
      authorityStatus: u.status,
      roleCode: u.userType,
    }));

    this.operatorAuthorities
      .updateAccountOperatorAuthorities(+this.targetUnitId, {
        accountOperatorAuthorityUpdateList: payload,
      })
      .subscribe();
  }

  private patchUsersForm(users: OperatorAuthorityInfoDTO[]): void {
    users.forEach((user, index) => {
      this.usersForm.controls.users.setControl(
        index,
        this.fb.group({ userId: user.userId, status: user.status, userType: user.roleCode }),
      );
    });
  }
}
