import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { DateInputComponent, TextareaComponent, WarningTextComponent } from '@netz/govuk-components';
import { FileInputComponent, WizardStepComponent } from '@shared/components';

import { SectorAssociationSchemeDetailsUpdateService, SectorAssociationSchemeDTO } from 'cca-api';

import { getCca3SchemeFromRoute } from '../scheme-tab.utils';
import {
  UMBRELLA_AGREEMENT_FORM,
  UmbrellaAgreementFormModel,
  UmbrellaAgreementFormProvider,
} from './umbrella-agreement-form.provider';

@Component({
  selector: 'cca-umbrella-agreement',
  templateUrl: './umbrella-agreement.component.html',
  imports: [
    PageHeadingComponent,
    WarningTextComponent,
    WizardStepComponent,
    FileInputComponent,
    ReactiveFormsModule,
    TextareaComponent,
    DateInputComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UmbrellaAgreementFormProvider],
})
export class UmbrellaAgreementComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorAssociationSchemeDetailsUpdateService = inject(SectorAssociationSchemeDetailsUpdateService);

  protected readonly form = inject<UmbrellaAgreementFormModel>(UMBRELLA_AGREEMENT_FORM);
  private readonly cca3Scheme = getCca3SchemeFromRoute(this.route) as SectorAssociationSchemeDTO;

  protected readonly getDownloadUrl = (uuid: string) => ['../sector-documents', uuid];

  onSubmit(): void {
    this.sectorAssociationSchemeDetailsUpdateService
      .updateSectorAssociationSchemeDetails(this.cca3Scheme.id, {
        umbrellaAgreementUuid: this.form.controls.file.value?.uuid,
        umaDate: new DatePipe('en-GB').transform(this.form.controls.umaDate.value, 'yyyy-MM-dd'),
        sectorDefinition: this.form.controls.sectorDefinition.value,
      })
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, fragment: 'scheme' }));
  }
}
