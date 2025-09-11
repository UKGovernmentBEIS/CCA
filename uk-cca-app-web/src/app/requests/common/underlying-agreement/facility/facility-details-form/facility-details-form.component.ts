import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CheckboxComponent,
  CheckboxesComponent,
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { ConfigService } from '@shared/config';
import { existingControlContainer } from '@shared/providers';

import { hasBothCCASchemes, isCCA2Scheme, isCCA3Scheme, isCreationDateAfterCutOffDate } from '../../../utils';
import { underlyingAgreementQuery } from '../../+state';
import { ApplicationReasonTypePipe } from '../../pipes';
import { FACILITY_DETAILS_FORM, FacilityDetailsFormModel } from './facility-details-form.provider';

@Component({
  selector: 'cca-facility-details-form',
  templateUrl: './facility-details-form.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    AccountAddressInputComponent,
    ApplicationReasonTypePipe,
    TextInputComponent,
    CheckboxesComponent,
    CheckboxComponent,
    WizardStepComponent,
    RouterLink,
  ],
  viewProviders: [existingControlContainer],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsFormComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly configService = inject(ConfigService);

  protected readonly submitChange = output<FormGroup<FacilityDetailsFormModel>>();

  protected readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly facility = this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId));
  protected readonly returnPath = this.facilityId ? '../../' : '../';

  protected readonly form = inject<FormGroup<FacilityDetailsFormModel>>(FACILITY_DETAILS_FORM);

  private readonly isAfterCutOffDate = isCreationDateAfterCutOffDate(
    this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)()?.creationDate,
    this.configService.getUnderlyingAgreementSchemeParticipationFlagCutOffDate(),
  );

  private readonly applicationReasonValue = toSignal(this.form.controls.applicationReason.valueChanges, {
    initialValue: this.form.value.applicationReason,
  });

  private readonly participatingSchemeVersionsValue = toSignal(
    this.form.controls.participatingSchemeVersions.valueChanges,
    {
      initialValue: this.form.value.participatingSchemeVersions,
    },
  );

  private readonly schemeParticipationChoiceStatus = toSignal(
    this.form.controls.schemeParticipationChoice.statusChanges,
    {
      initialValue: this.form.controls.schemeParticipationChoice.status,
    },
  );

  protected readonly showSchemeParticipationChoice = computed(
    () =>
      !this.isAfterCutOffDate &&
      this.applicationReasonValue() === 'CHANGE_OF_OWNERSHIP' &&
      this.schemeParticipationChoiceStatus() !== 'DISABLED',
  );

  protected readonly showChangeOfOwnershipCtrls = computed(
    () => this.applicationReasonValue() === 'CHANGE_OF_OWNERSHIP',
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
