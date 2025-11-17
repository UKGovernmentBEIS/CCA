import { ChangeDetectionStrategy, Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { catchError, of, take } from 'rxjs';

import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';
import { OperatorTypePipe, StatusPipe, transformAddress } from '@shared/pipes';
import { SchemeVersion } from '@shared/types';
import { equalAddressFields, equalArrayFields, equalFields, transformPhoneNumber } from '@shared/utils';

import { CompaniesInformationService, CompanyProfileDTO } from 'cca-api';

import { ActiveTargetUnitStore } from '../../active-target-unit.store';
import { SchemeDetailsComponent } from './scheme-details/scheme-details.component';

export type IsEditableData = {
  isEditable: boolean;
  isFinancialIndependenceEditable: boolean;
};

@Component({
  selector: 'cca-details-summary',
  templateUrl: './details-summary.component.html',
  imports: [
    ReactiveFormsModule,
    UtilityPanelComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    RouterLink,
    StatusPipe,
    OperatorTypePipe,
    SchemeDetailsComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsSummaryComponent implements OnInit {
  private readonly companiesInformationService = inject(CompaniesInformationService);
  private readonly activeTargetUnitStore = inject(ActiveTargetUnitStore);

  protected readonly isEditableData = input.required<IsEditableData>();

  private readonly accountDetails = this.activeTargetUnitStore.stateAsSignal;

  protected readonly toggleCompaniesHouseDetailsCtrl = new FormControl<boolean>(false);
  protected readonly toggleCompaniesHouseDetails = toSignal(this.toggleCompaniesHouseDetailsCtrl.valueChanges, {
    initialValue: false,
  });

  protected readonly mainColumnClass = computed(() =>
    this.toggleCompaniesHouseDetails() ? 'govuk-grid-column-two-thirds' : 'govuk-grid-column-full',
  );

  private readonly companiesHouseDetailsResponse = signal<CompanyProfileDTO | null>(null);

  protected readonly companiesHouseState = computed(() => {
    const response = this.companiesHouseDetailsResponse();
    return {
      details: typeof response === 'object' ? response : null,
      error: typeof response === 'number' ? response : null,
      address: typeof response === 'object' ? transformAddress(response?.address).join('\n') : null,
    };
  });

  protected readonly downloadURL = computed(
    () => `./${this.accountDetails().underlyingAgreementDetails?.id}/file-download`,
  );

  protected readonly tuDetails = computed(() => this.accountDetails().targetUnitAccountDetails);
  protected readonly unaDetails = computed(() => this.accountDetails().underlyingAgreementDetails);
  protected readonly subsectorAssociation = computed(() => this.accountDetails().subsectorAssociation);

  protected readonly responsiblePerson = computed(() => this.tuDetails().responsiblePerson);
  protected readonly administrativePerson = computed(() => this.tuDetails().administrativeContactDetails);

  protected readonly addresses = computed(() => ({
    operator: transformAddress(this.tuDetails().address).join('\n'),
    responsiblePerson: transformAddress(this.responsiblePerson().address).join('\n'),
    administrativePerson: transformAddress(this.administrativePerson().address).join('\n'),
  }));

  protected readonly phoneNumbers = computed(() => ({
    responsiblePerson: transformPhoneNumber(this.responsiblePerson().phoneNumber),
    administrativePerson: transformPhoneNumber(this.administrativePerson().phoneNumber),
  }));

  protected readonly cca2UnderlyingAgreementDetails = computed(
    () => this.unaDetails()?.underlyingAgreementDocumentMap?.[SchemeVersion.CCA_2],
  );

  protected readonly cca3UnderlyingAgreementDetails = computed(
    () => this.unaDetails()?.underlyingAgreementDocumentMap?.[SchemeVersion.CCA_3],
  );

  protected readonly fieldDiffs = computed(() => {
    const companiesHouse = this.companiesHouseState().details;
    if (!companiesHouse) return {};

    const tuDetails = this.tuDetails();

    return {
      name: equalFields(tuDetails.name, companiesHouse.name),
      operatorType: equalFields(tuDetails.operatorType, companiesHouse.operatorType),
      companyRegistrationNumber: equalFields(tuDetails.companyRegistrationNumber, companiesHouse.registrationNumber),
      sicCodes: equalArrayFields(tuDetails.sicCodes, companiesHouse.sicCodes),
      address: equalAddressFields(transformAddress(tuDetails.address), transformAddress(companiesHouse.address)),
    };
  });

  ngOnInit() {
    if (this.accountDetails().targetUnitAccountDetails.companyRegistrationNumber) {
      this.companiesInformationService
        .getCompanyProfileByRegistrationNumber(this.accountDetails().targetUnitAccountDetails.companyRegistrationNumber)
        .pipe(
          take(1),
          catchError((err) => of(err.status)),
        )
        .subscribe((res) => {
          if (typeof res === 'object') {
            this.companiesHouseDetailsResponse.set(res);
          }
        });
    }
  }

  protected getDiffClass(areEqual: boolean): string {
    if (!this.toggleCompaniesHouseDetails() || !this.companiesHouseState().details) return '';
    return areEqual ? '' : 'field-diff';
  }
}
