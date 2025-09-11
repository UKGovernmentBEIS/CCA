import { LowerCasePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { transformUsername } from '@netz/common/pipes';
import {
  ButtonDirective,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
  TagComponent,
} from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { StatusColorPipe } from '@shared/pipes';

import {
  SectorAssociationAuthoritiesService,
  SectorAssociationTargetUnitAccountsInfoService,
  TargetUnitAccountInfoDTO,
  TargetUnitAccountInfoResponseDTO,
} from 'cca-api';

type TargetUnitFormModel = FormGroup<{
  id: FormControl<number>;
  name: FormControl<string>;
  targetUnitID: FormControl<string>;
  assignedTo: FormControl<string>;
  status: FormControl<TargetUnitAccountInfoDTO['status']>;
}>;

type State = {
  targetUnits: TargetUnitAccountInfoDTO[];
  editable: boolean;
  totalItems: number;
};

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 50;

@Component({
  selector: 'cca-sector-target-units-tab',
  templateUrl: './target-units-tab.component.html',
  standalone: true,
  imports: [
    RouterLink,
    ReactiveFormsModule,
    TableComponent,
    SelectComponent,
    ButtonDirective,
    TagComponent,
    StatusColorPipe,
    PaginationComponent,
    LowerCasePipe,
    UpperCasePipe,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorTargetUnitsTabComponent {
  private readonly sectorAssociationAuthoritiesService = inject(SectorAssociationAuthoritiesService);
  private readonly sectorAssociationTargetUnitAccountsService = inject(SectorAssociationTargetUnitAccountsInfoService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  private readonly userId = inject(AuthStore).select(selectUserId);
  private readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');

  private readonly queryParams = toSignal(this.activatedRoute.queryParamMap);

  private sectorUsersAuthorities = toSignal(
    this.sectorAssociationAuthoritiesService
      .getSectorUserAuthoritiesBySectorAssociationId(this.sectorId)
      .pipe(map((r) => r.authorities.filter((a) => a.status === 'ACTIVE'))),
  );

  protected readonly targetUnitsColumns: GovukTableColumn[] = [
    { field: 'accountName', header: 'Name' },
    { field: 'businessId', header: 'Target Unit ID' },
    { field: 'assignedTo', header: 'Assigned to' },
    { field: 'status', header: 'Status' },
  ];

  readonly state = signal<State>({
    editable: false,
    targetUnits: [],
    totalItems: 0,
  });

  readonly currentPage = computed(() => {
    const params = this.queryParams();
    return +params?.get('page') || DEFAULT_PAGE;
  });

  readonly pageSize = computed(() => {
    const params = this.queryParams();
    return +params?.get('pageSize') || DEFAULT_PAGE_SIZE;
  });

  protected readonly editable = computed(() => this.state().editable);

  protected readonly targetUnits = computed(() => this.state().targetUnits);

  protected readonly sectorNames = computed(() => {
    const namesMap = new Map();
    const authorities = this.sectorUsersAuthorities();

    if (!authorities?.length) return namesMap;

    for (const user of authorities) {
      namesMap.set(user.userId, transformUsername(user));
    }

    return namesMap;
  });

  protected readonly sectorUserOptions = computed(() =>
    this.sectorUsersAuthorities()
      ? [{ text: 'Unassigned', value: null }].concat(
          this.sectorUsersAuthorities()?.map((a) => ({
            text: transformUsername(a),
            value: a.userId,
          })),
        )
      : [],
  );

  protected readonly canCreateTargetUnit = computed(
    () => !!this.sectorUsersAuthorities()?.find((u) => u.userId === this.userId()),
  );

  protected readonly targetUnitsForm = this.fb.group({
    targetUnits: this.fb.array<TargetUnitFormModel>([]),
  });

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        map((params) => ({
          page: +params.get('page') || DEFAULT_PAGE,
          pageSize: +params.get('pageSize') || DEFAULT_PAGE_SIZE,
        })),
        switchMap(({ page, pageSize }) =>
          this.sectorAssociationTargetUnitAccountsService.getTargetUnitAccountsWithSiteContacts(
            this.sectorId,
            page - 1,
            pageSize,
          ),
        ),
        tap(this.updateState),
      )
      .subscribe();
  }

  refresh() {
    this.fetchTargetUnits().pipe(tap(this.updateState)).subscribe();
  }

  fetchTargetUnits() {
    return this.sectorAssociationTargetUnitAccountsService.getTargetUnitAccountsWithSiteContacts(
      this.sectorId,
      this.currentPage() - 1,
      this.pageSize(),
    );
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ pageSize });
  }

  onAddNewTargetUnit() {
    this.router.navigate(['target-units', 'create'], {
      relativeTo: this.activatedRoute,
    });
  }

  onSubmit() {
    this.sectorAssociationTargetUnitAccountsService
      .updateTargetUnitAccountSiteContacts(
        this.sectorId,
        this.targetUnitsForm.getRawValue().targetUnits.map((tu) => ({
          accountId: tu.id,
          userId: tu.assignedTo,
        })),
      )
      .subscribe();
  }

  private updateState = ({
    editable,
    accountsWithSiteContact: targetUnits,
    totalItems,
  }: TargetUnitAccountInfoResponseDTO) => {
    this.state.update(() => ({ editable, targetUnits, totalItems }));
    this.patchTargetUnitsForm(targetUnits);
  };

  private patchTargetUnitsForm(targetUnits: TargetUnitAccountInfoDTO[]) {
    const formArray = this.targetUnitsForm.controls.targetUnits;

    formArray.clear();

    for (const tu of targetUnits) {
      formArray.push(
        this.fb.group({
          id: [tu.accountId],
          name: [tu.businessId],
          status: [tu.status],
          assignedTo: [tu.siteContactUserId],
          targetUnitID: [tu.accountName],
        }),
      );
    }
  }

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: this.activatedRoute.snapshot.fragment,
    });
  }
}
