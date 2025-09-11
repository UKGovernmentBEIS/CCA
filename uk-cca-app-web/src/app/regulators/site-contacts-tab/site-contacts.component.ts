import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PendingButtonDirective } from '@netz/common/directives';
import { transformUsername } from '@netz/common/pipes';
import { ButtonDirective, GovukTableColumn, SelectComponent, TableComponent } from '@netz/govuk-components';
import { PaginationComponent } from '@shared/components';

import {
  RegulatorAuthoritiesService,
  SectorAssociationSiteContactInfoDTO,
  SectorAssociationSiteContactInfoResponse,
  SectorAssociationsSiteContactsService,
} from 'cca-api';

import { savePartiallyNotFoundSiteContactError } from '../errors/business-error';
import { createForm } from './site-contact.utils';

type State = {
  siteContacts: SectorAssociationSiteContactInfoDTO[];
  isEditable: boolean;
  totalItems: number;
};

const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 20;

@Component({
  selector: 'cca-site-contacts',
  templateUrl: './site-contacts.component.html',
  standalone: true,
  imports: [
    TableComponent,
    SelectComponent,
    ReactiveFormsModule,
    PaginationComponent,
    NgTemplateOutlet,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SiteContactsComponent {
  private readonly fb = inject(FormBuilder);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorAssociationsSiteContactsService = inject(SectorAssociationsSiteContactsService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly businessErrorService = inject(BusinessErrorService);

  private readonly queryParams = toSignal(this.activatedRoute.queryParamMap);

  protected readonly columns: GovukTableColumn<SectorAssociationSiteContactInfoDTO>[] = [
    { field: 'sectorName', header: 'Sector Name', isHeader: true },
    { field: 'sectorAssociationId', header: 'Assigned to' },
  ];

  readonly state = signal<State>({
    siteContacts: [],
    isEditable: false,
    totalItems: 0,
  });

  protected readonly regulators = toSignal(
    this.regulatorAuthoritiesService.getCaRegulators().pipe(map((r) => r.caUsers)),
  );

  readonly currentPage = computed(() => {
    const params = this.queryParams();
    return +params?.get('page') || DEFAULT_PAGE;
  });

  readonly pageSize = computed(() => {
    const params = this.queryParams();
    return +params?.get('pageSize') || DEFAULT_PAGE_SIZE;
  });

  protected readonly regulatorNames = computed(() => {
    const namesMap = new Map();
    if (!this.regulators()) return namesMap;

    for (const regulator of this.regulators()) {
      namesMap.set(regulator.userId, transformUsername(regulator));
    }

    return namesMap;
  });

  protected readonly form = computed(() => createForm(this.fb, this.state().siteContacts));

  readonly assigneeOptions = computed(() =>
    this.regulators()?.length
      ? [{ text: 'Unassigned', value: null }].concat(
          this.regulators()
            .filter((u) => u.authorityStatus === 'ACTIVE')
            .map((r) => ({ text: transformUsername(r), value: r.userId })),
        )
      : [],
  );

  constructor() {
    this.activatedRoute.queryParamMap
      .pipe(
        takeUntilDestroyed(),
        map((queryParamMap) => ({
          page: +queryParamMap.get('page') || DEFAULT_PAGE,
          pageSize: +queryParamMap.get('pageSize') || DEFAULT_PAGE_SIZE,
        })),
        switchMap(({ page, pageSize }) => this.fetchSiteContacts(page, pageSize)),
      )
      .subscribe((resp) => {
        this.update(resp);
      });
  }

  onPageChange(page: number) {
    if (page === this.currentPage()) return;
    this.handleQueryParamsNavigation({ page });
  }

  onPageSizeChange(pageSize: number) {
    if (pageSize === this.pageSize()) return;
    this.handleQueryParamsNavigation({ pageSize });
  }

  refreshSectorAssociationSiteContacts() {
    this.fetchSiteContacts(this.currentPage(), this.pageSize())
      .pipe(
        tap((resp) => {
          this.update(resp);
        }),
      )
      .subscribe((resp) => {
        this.form().patchValue({ siteContacts: resp.siteContacts });
        window.scrollTo({ top: 0, behavior: 'smooth' });
      });
  }

  onSave() {
    const siteContacts = this.form().controls.siteContacts.value;

    this.sectorAssociationsSiteContactsService
      .updateSectorAssociationSiteContacts(siteContacts)
      .pipe(
        catchBadRequest([ErrorCodes.AUTHORITY1003, ErrorCodes.ACCOUNT1004], () =>
          this.businessErrorService.showError(savePartiallyNotFoundSiteContactError),
        ),
      )
      .subscribe();
  }

  private fetchSiteContacts(page: number, pageSize: number) {
    return this.sectorAssociationsSiteContactsService.getSectorAssociationSiteContacts(page - 1, pageSize);
  }

  private update = (response: SectorAssociationSiteContactInfoResponse) => {
    const sortedContacts = response.siteContacts.sort((a, b) => a.sectorName.localeCompare(b.sectorName));

    this.state.update(() => ({
      siteContacts: sortedContacts,
      isEditable: response.editable,
      totalItems: response.totalItems,
    }));
  };

  private handleQueryParamsNavigation(pagination: Partial<{ page: number; pageSize: number }>) {
    this.router.navigate([], {
      queryParams: { ...pagination },
      queryParamsHandling: 'merge',
      relativeTo: this.activatedRoute,
      fragment: 'site-contacts',
    });
  }
}
