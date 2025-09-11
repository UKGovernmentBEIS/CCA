import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { ButtonDirective, GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';

import { BuyOutAndSurplusInfoService } from 'cca-api';

import { BuyoutAndSurplusTabStore } from '../buyout-and-surplus-tab.store';

@Component({
  selector: 'cca-edit-surplus-on-hold',
  templateUrl: './edit-on-hold.component.html',
  standalone: true,
  imports: [ButtonDirective, FormsModule, RadioComponent, RadioOptionComponent, ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditOnHoldComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly buyoutAndSurplusTabStore = inject(BuyoutAndSurplusTabStore);
  private readonly buyOutAndSurplusInfoService = inject(BuyOutAndSurplusInfoService);

  private readonly excluded = this.buyoutAndSurplusTabStore.state?.surplusInfo?.excluded;
  private readonly accountId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  readonly form = new FormGroup({
    changeOnHold: new FormControl(this.excluded ?? false, [GovukValidators.required('Please select an option')]),
  });

  onSubmit() {
    const serviceCall = this.form.value.changeOnHold
      ? this.buyOutAndSurplusInfoService.excludeAccountFromBuyOutSurplus(this.accountId)
      : this.buyOutAndSurplusInfoService.removeAccountExclusionFromBuyOutSurplus(this.accountId);

    serviceCall
      .pipe(
        catchError((err) => {
          console.error(err);
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute });
      });
  }
}
