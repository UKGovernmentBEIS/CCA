import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { FileUploadEvent } from '@shared/components';
import { futureDateValidator } from '@shared/validators';

import { SectorAssociationSchemeDTO } from 'cca-api';

import { getCca3SchemeFromRoute } from '../scheme-tab.utils';
import { SectorSchemeDocumentFileService } from '../sector-scheme-document-file.service';

export type UmbrellaAgreementFormModel = FormGroup<{
  file: FormControl<FileUploadEvent>;
  umaDate: FormControl<Date>;
  sectorDefinition: FormControl<string>;
}>;

export const UMBRELLA_AGREEMENT_FORM = new InjectionToken<UmbrellaAgreementFormModel>('Umbrella agreement form');

export const UmbrellaAgreementFormProvider: Provider = {
  provide: UMBRELLA_AGREEMENT_FORM,
  deps: [FormBuilder, SectorSchemeDocumentFileService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    sectorSchemeDocumentFileService: SectorSchemeDocumentFileService,
    route: ActivatedRoute,
  ) => {
    const cca3Scheme = getCca3SchemeFromRoute(route) as SectorAssociationSchemeDTO;

    return fb.group({
      file: sectorSchemeDocumentFileService.buildFormControl(cca3Scheme?.umbrellaAgreement, true),
      umaDate: fb.control(cca3Scheme?.umaDate ? new Date(cca3Scheme.umaDate) : null, [
        GovukValidators.required('Enter the umbrella agreement date.'),
        futureDateValidator('The date cannot be in the future.'),
      ]),
      sectorDefinition: fb.control(cca3Scheme?.sectorDefinition ?? null, [
        GovukValidators.required('Enter the sector definition.'),
      ]),
    });
  },
};
