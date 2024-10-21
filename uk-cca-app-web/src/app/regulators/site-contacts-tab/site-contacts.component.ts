import { NgTemplateOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { distinctUntilChanged, map, Observable, switchMap, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { transformUsername, UserFullNamePipe } from '@netz/common/pipes';
import { ButtonDirective, GovukTableColumn, SelectComponent, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import {
  RegulatorAuthoritiesService,
  RegulatorUserAuthorityInfoDTO,
  SectorAssociationSiteContactDTO,
  SectorAssociationSiteContactInfoDTO,
  SectorAssociationsSiteContactsService,
} from 'cca-api';

import { savePartiallyNotFoundSiteContactError } from '../errors/business-error';
import { createForm } from './site-contact.utils';

type State = {
  currentPage: number;
  siteContacts: SectorAssociationSiteContactInfoDTO[];
  editable: boolean;
  totalItems: number;
};

@Component({
  selector: 'cca-site-contacts',
  templateUrl: './site-contacts.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  providers: [DestroySubject],
  imports: [
    TableComponent,
    SelectComponent,
    ReactiveFormsModule,
    PaginationComponent,
    NgTemplateOutlet,
    ButtonDirective,
    UserFullNamePipe,
    PendingButtonDirective,
  ],
})
export class SiteContactsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly sectorAssociationsSiteContactsService = inject(SectorAssociationsSiteContactsService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly destroy = inject(DestroyRef);

  readonly pageSize = 50;

  readonly regulators = toSignal(this.getRegulators());
  readonly regulatorNames = computed(() => {
    const namesMap = new Map();
    if (!this.regulators()) return namesMap;
    this.regulators().forEach((r) => namesMap.set(r.userId, transformUsername(r)));
    return namesMap;
  });

  columns: GovukTableColumn<SectorAssociationSiteContactInfoDTO>[] = [
    { field: 'sectorName', header: 'Sector Name', isHeader: true },
    { field: 'sectorAssociationId', header: 'Assigned to' },
  ];

  private readonly state: WritableSignal<State> = signal({
    currentPage: +this.route.snapshot.queryParamMap.get('page') || 1,
    siteContacts: [],
    editable: false,
    totalItems: 0,
  });

  siteContacts = computed(() => this.state().siteContacts);
  count = computed(() => this.state().totalItems);
  currentPage = computed(() => this.state().currentPage);
  currentPage$ = toObservable(this.currentPage);
  editable = computed(() => this.state().editable);
  form = computed(() => createForm(this.fb, this.siteContacts()));
  assigneeOptions = computed(() =>
    this.regulators()?.length
      ? [{ text: 'Unassigned', value: null }].concat(
          this.regulators()
            .filter((u) => u.authorityStatus === 'ACTIVE')
            .map((r) => ({ text: transformUsername(r), value: r.userId })),
        )
      : [],
  );

  ngOnInit(): void {
    // we need this logic because pagination component emits twice when bootstrapped
    this.currentPage$
      .pipe(
        takeUntilDestroyed(this.destroy),
        distinctUntilChanged(),
        switchMap((page) =>
          this.sectorAssociationsSiteContactsService.getSectorAssociationSiteContacts(page - 1, this.pageSize).pipe(
            tap((v) => {
              const sortedContacts = v.siteContacts.sort((a, b) => {
                return a.sectorName.localeCompare(b.sectorName);
              });

              this.state.update((state) => ({
                ...state,
                siteContacts: sortedContacts,
                editable: v.editable,
                totalItems: v.totalItems,
              }));
            }),
          ),
        ),
      )
      .subscribe();
  }
  private getRegulators(): Observable<RegulatorUserAuthorityInfoDTO[]> {
    return this.regulatorAuthoritiesService.getCaRegulators().pipe(map((r) => r.caUsers));
  }
  handlePageChange(page: number) {
    this.state.update((state) => ({ ...state, currentPage: page }));
  }

  refreshSectorAssociationSiteContacts() {
    this.sectorAssociationsSiteContactsService
      .getSectorAssociationSiteContacts(this.currentPage() - 1, this.pageSize)
      .subscribe((v) => {
        const sortedContacts = v.siteContacts.sort((a, b) => {
          return a.sectorName.localeCompare(b.sectorName);
        });

        this.state.update((state) => ({ ...state, siteContacts: sortedContacts, totalItems: v.totalItems }));
      });
  }

  onSave(): void {
    const siteContacts = this.form().controls.siteContacts.value as SectorAssociationSiteContactDTO[];
    this.sectorAssociationsSiteContactsService
      .updateSectorAssociationSiteContacts(siteContacts)
      .pipe(
        catchBadRequest([ErrorCodes.AUTHORITY1003, ErrorCodes.ACCOUNT1004], () =>
          this.businessErrorService.showError(savePartiallyNotFoundSiteContactError),
        ),
      )
      .subscribe();
  }
}
