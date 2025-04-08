import { LowerCasePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { distinctUntilChanged, map, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { transformUsername } from '@netz/common/pipes';
import {
  ButtonDirective,
  GovukTableColumn,
  SelectComponent,
  SortEvent,
  TableComponent,
  TagComponent,
} from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { TargetUnitStatusColorPipe } from '@shared/pipes';

import {
  SectorAssociationAuthoritiesService,
  TargetUnitAccountInfoDTO,
  TargetUnitAccountInfoResponseDTO,
  TargetUnitAccountInfoViewService,
  TargetUnitAccountsSiteContactsService,
} from 'cca-api';

type TargetUnitFormModel = FormGroup<{
  id: FormControl<number>;
  name: FormControl<string>;
  targetUnitID: FormControl<string>;
  assignedTo: FormControl<string>;
  status: FormControl<TargetUnitAccountInfoDTO['status']>;
}>;

type State = {
  currentPage: number;
  targetUnits: TargetUnitAccountInfoDTO[];
  editable: boolean;
  totalItems: number;
};

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
    TargetUnitStatusColorPipe,
    PaginationComponent,
    LowerCasePipe,
    UpperCasePipe,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorTargetUnitsTabComponent {
  private readonly sectorAssociationAuthoritiesService = inject(SectorAssociationAuthoritiesService);
  private readonly targetUnitService = inject(TargetUnitAccountInfoViewService);
  private readonly targetUnitSiteContactsService = inject(TargetUnitAccountsSiteContactsService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  private readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');
  private readonly userId = inject(AuthStore).select(selectUserId);

  readonly pageSize = 50;

  private readonly state = signal<State>({
    currentPage: +this.activatedRoute.snapshot.queryParamMap.get('page') || 1,
    editable: true,
    targetUnits: [],
    totalItems: 0,
  });

  protected readonly editable = computed(() => this.state().editable);

  protected readonly targetUnits = computed(() => {
    const tus = this.state().targetUnits;
    const sorting = this.sorting();

    if (!sorting) return tus;

    return tus.sort((a, b) => {
      const diff = a[sorting.column]?.localeCompare(b[sorting.column], 'en-GB', {
        numeric: true,
        sensitivity: 'base',
      });

      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    });
  });

  readonly sectorNames = computed(() => {
    const namesMap = new Map();

    if (!this.sectorUsersAuthorities()) return namesMap;

    this.sectorUsersAuthorities().forEach((r) => namesMap.set(r.userId, transformUsername(r)));
    return namesMap;
  });

  private readonly currentPage = computed(() => this.state().currentPage);
  private readonly currentPage$ = toObservable(this.currentPage);
  protected readonly count = computed(() => this.state().totalItems);
  private readonly sorting = signal<SortEvent>({ column: 'id', direction: 'ascending' });

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

  canCreateTargetUnit = computed(() => !!this.sectorUsersAuthorities()?.find((u) => u.userId === this.userId()));

  protected readonly targetUnitsForm = this.fb.group({
    targetUnits: this.fb.array<TargetUnitFormModel>([]),
  });

  constructor() {
    effect(() => {
      this.patchTargetUnitsForm(this.targetUnits());
    });

    this.currentPage$
      .pipe(
        takeUntilDestroyed(),
        distinctUntilChanged(),
        switchMap((page) =>
          this.targetUnitService.getTargetUnitAccountsWithSiteContacts(this.sectorId, page - 1, this.pageSize),
        ),
        tap(this.update),
      )
      .subscribe();
  }

  private update = ({
    editable,
    accountsWithSiteContact: targetUnits,
    totalItems,
  }: TargetUnitAccountInfoResponseDTO): void => {
    this.state.update((state) => ({
      ...state,
      editable,
      targetUnits,
      totalItems,
    }));
  };

  private patchTargetUnitsForm(targetUnits: TargetUnitAccountInfoDTO[]) {
    targetUnits.forEach((tu, idx) => {
      this.targetUnitsForm.controls.targetUnits.setControl(
        idx,
        this.fb.group({
          id: tu.accountId,
          name: tu.businessId,
          status: tu.status,
          assignedTo: tu.siteContactUserId,
          targetUnitID: tu.accountName,
        }),
      );
    });
  }

  refresh(): void {
    this.fetchTargetUnits().subscribe(this.update);
  }

  fetchTargetUnits() {
    return this.targetUnitService.getTargetUnitAccountsWithSiteContacts(
      this.sectorId,
      this.state().currentPage - 1,
      this.pageSize,
    );
  }

  handlePageChange(page: number) {
    this.state.update((state) => ({ ...state, currentPage: page }));
  }

  onAddNewTargetUnit() {
    this.router.navigate(['target-units', 'create'], {
      relativeTo: this.activatedRoute,
    });
  }

  onSubmit() {
    this.targetUnitSiteContactsService
      .updateTargetUnitAccountSiteContacts(
        this.sectorId,
        this.targetUnitsForm.getRawValue().targetUnits.map((tu) => ({
          accountId: tu.id,
          userId: tu.assignedTo,
        })),
      )
      .subscribe();
  }
}
