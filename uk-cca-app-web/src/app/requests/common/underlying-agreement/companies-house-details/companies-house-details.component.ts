import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { SummaryListComponent, SummaryListRowDirective, SummaryListRowValueDirective } from '@netz/govuk-components';
import { UtilityPanelComponent } from '@shared/components';
import { transformAddress } from '@shared/pipes';
import { equalAddressFields, equalFields } from '@shared/utils';

import { CompanyProfileDTO, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

export type CompaniesHouseState = {
  details: CompanyProfileDTO;
  error: any;
  address: string;
};

@Component({
  selector: 'cca-companies-house-details',
  templateUrl: './companies-house-details.component.html',
  imports: [UtilityPanelComponent, SummaryListComponent, SummaryListRowDirective, SummaryListRowValueDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompaniesHouseDetailsComponent {
  protected readonly toggleCompaniesHouseDetails = input.required<boolean>();
  protected readonly companiesHouseState = input.required<CompaniesHouseState>();
  protected readonly tuDetails = input.required<UnderlyingAgreementTargetUnitDetails>();

  protected readonly fieldDiffs = computed(() => {
    const companiesHouse = this.companiesHouseState().details;
    if (!companiesHouse) return {};

    const tuDetails = this.tuDetails();

    return {
      name: equalFields(tuDetails.operatorName, companiesHouse.name),
      operatorType: equalFields(tuDetails.operatorType, companiesHouse.operatorType),
      companyRegistrationNumber: equalFields(tuDetails.companyRegistrationNumber, companiesHouse.registrationNumber),
      address: equalAddressFields(
        transformAddress(tuDetails.operatorAddress),
        transformAddress(companiesHouse.address),
      ),
    };
  });

  protected getDiffClass(areEqual: boolean): string {
    if (!this.toggleCompaniesHouseDetails() || !this.companiesHouseState().details) return '';
    return areEqual ? '' : 'field-diff';
  }
}
