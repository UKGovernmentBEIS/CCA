import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { debounceTime, map } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CheckboxComponent,
  CheckboxesComponent,
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { FacilityAddressInputComponent, WizardStepComponent } from '@shared/components';
import { ConfigService } from '@shared/config';
import { UK_COUNTRY_CODES } from '@shared/services';

import { hasBothCCASchemes, isCCA2Scheme, isCCA3Scheme, isCreationDateAfterCutOffDate } from '../../../utils';
import { underlyingAgreementQuery } from '../../+state';
import { ApplicationReasonTypePipe } from '../../pipes';
import {
  VARIATION_FACILITY_DETAILS_FORM,
  VariationFacilityDetailsFormModel,
} from './variation-facility-details-form.provider';

@Component({
  selector: 'cca-variation-facility-details-form',
  templateUrl: './variation-facility-details-form.component.html',
  imports: [
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    FacilityAddressInputComponent,
    ApplicationReasonTypePipe,
    TextInputComponent,
    CheckboxesComponent,
    CheckboxComponent,
    WizardStepComponent,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationFacilityDetailsFormComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly configService = inject(ConfigService);

  protected readonly submitChange = output<FormGroup<VariationFacilityDetailsFormModel>>();

  protected readonly form = inject<FormGroup<VariationFacilityDetailsFormModel>>(VARIATION_FACILITY_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly facility = this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId));
  protected readonly returnPath = this.facilityId ? '../../' : '../';

  private readonly tuDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  );

  protected readonly hasUKAddress = computed(() =>
    UK_COUNTRY_CODES.includes(this.tuDetails()?.operatorAddress?.country),
  );

  private readonly participatingSchemeVersionsValue = toSignal(
    this.form.controls.participatingSchemeVersions.valueChanges,
    {
      initialValue: this.form.value.participatingSchemeVersions,
    },
  );

  private readonly isAfterCutOffDate = computed(() =>
    isCreationDateAfterCutOffDate(
      this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)()?.creationDate,
      this.configService.getUnderlyingAgreementSchemeParticipationFlagCutOffDate(),
    ),
  );

  private readonly applicationReasonValue = toSignal(this.form.controls.applicationReason.valueChanges, {
    initialValue: this.form.value.applicationReason,
  });

  private readonly schemeParticipationChoiceEnabled = toSignal(
    this.form.controls.schemeParticipationChoice.statusChanges.pipe(
      map((status) => status !== 'DISABLED'),
      // Small delay to ensure the form control status is fully updated
      debounceTime(0),
    ),
    {
      initialValue: this.form.controls.schemeParticipationChoice.status !== 'DISABLED',
    },
  );

  protected readonly showPreviousFacilityIdCtrl = computed(() => {
    const facility = this.facility();
    const applicationReason = this.applicationReasonValue();

    const facilityStatus = facility?.status || 'NEW';

    return (
      (facilityStatus === 'NEW' && applicationReason === 'CHANGE_OF_OWNERSHIP') ||
      (facilityStatus === 'LIVE' &&
        facility?.facilityDetails?.applicationReason === 'CHANGE_OF_OWNERSHIP' &&
        facility?.facilityDetails?.previousFacilityId)
    );
  });

  protected readonly showSchemeParticipationChoice = computed(() => {
    const isAfterCutOff = this.isAfterCutOffDate();
    const showChangeOfOwnership = this.showChangeOfOwnershipCtrls();
    const schemeChoiceEnabled = this.schemeParticipationChoiceEnabled();

    return !isAfterCutOff && showChangeOfOwnership && schemeChoiceEnabled;
  });

  protected readonly showChangeOfOwnershipCtrls = computed(
    () => this.applicationReasonValue() === 'CHANGE_OF_OWNERSHIP' || this.facility()?.status === 'LIVE',
  );

  protected readonly schemeParticipation = computed(() => {
    if (this.applicationReasonValue() === 'NEW_AGREEMENT') return 'CCA3';

    const participatingSchemes = this.participatingSchemeVersionsValue();
    if (!participatingSchemes || participatingSchemes.length === 0) return '';

    if (hasBothCCASchemes(participatingSchemes)) {
      return 'Both';
    } else if (isCCA3Scheme(participatingSchemes)) {
      return 'CCA3';
    } else if (isCCA2Scheme(participatingSchemes)) {
      return 'CCA2';
    }

    return '';
  });

  onSubmit() {
    this.submitChange.emit(this.form);
  }
}
