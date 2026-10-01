import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of, switchMap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  GovukSelectOption,
  GovukTableColumn,
  SelectComponent,
  TableComponent,
  TagComponent,
  WarningTextComponent,
} from '@netz/govuk-components';
import { ErrorSummaryComponent, ErrorSummaryInfo } from '@shared/components';
import { isCCA3TargetPeriod, TargetPeriodType } from '@shared/types';

import {
  AvailableTargetPeriodsBuyOutDTO,
  BuyOutAndSurplusInfoService,
  BuyOutSurplusFacilityRunCreateActionPayload,
  RequestsService,
  TargetUnitAccountBusinessInfoDTO,
} from 'cca-api';

import { NEW_BATCH_FORM, NewBatchFormModel, NewBatchFormProvider } from './new-batch-form.provider';
import { buyOutRunCreateActionFor, sortTargetPeriodsDesc, TargetPeriodBuyOutDetails } from './utils';

@Component({
  selector: 'cca-new-batch',
  templateUrl: './new-batch.component.html',
  imports: [
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    WarningTextComponent,
    ReactiveFormsModule,
    SelectComponent,
    TableComponent,
    TagComponent,
    DecimalPipe,
    ErrorSummaryComponent,
  ],
  providers: [NewBatchFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NewBatchComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestsService = inject(RequestsService);
  private readonly buyOutAndSurplusInfoService = inject(BuyOutAndSurplusInfoService);

  protected readonly form = inject<NewBatchFormModel>(NEW_BATCH_FORM);

  private readonly availableTargetPeriods = signal(
    this.activatedRoute.snapshot.data.availableTargetPeriods as AvailableTargetPeriodsBuyOutDTO,
  );

  private readonly currentTargetPeriods = computed(() =>
    sortTargetPeriodsDesc(this.availableTargetPeriods()?.currentTargetPeriods),
  );

  /** Only CCA3 target periods carry a buy-out cost, so a stray CCA2 one never reaches the cost table. */
  private readonly previousTargetPeriods = computed(() =>
    sortTargetPeriodsDesc(this.availableTargetPeriods()?.previousTargetPeriods).filter(({ businessId }) =>
      isCCA3TargetPeriod(businessId),
    ),
  );

  protected readonly selectedTargetPeriod = toSignal(this.form.controls.targetPeriodType.valueChanges, {
    initialValue: this.form.controls.targetPeriodType.value,
  });

  /** A single available target period is applied automatically, more than one has to be picked by the regulator. */
  protected readonly hasTargetPeriodChoice = computed(() => this.currentTargetPeriods().length > 1);

  protected readonly targetPeriodOptions = computed<GovukSelectOption<TargetPeriodType>[]>(() =>
    this.currentTargetPeriods().map(({ businessId }) => ({ value: businessId, text: businessId })),
  );

  /**
   * CCA3 calculates per facility and carries a buy-out cost per target period, CCA2 calculates per target unit
   * and has neither, so this drives both the wording of the page and the cost block.
   */
  protected readonly isCca3Run = computed(() => isCCA3TargetPeriod(this.selectedTargetPeriod()));

  protected readonly buyOutCosts = computed<TargetPeriodBuyOutDetails[]>(() => {
    if (!this.isCca3Run()) return [];

    const selectedTargetPeriod = this.selectedTargetPeriod();
    const selected = this.currentTargetPeriods().find(({ businessId }) => businessId === selectedTargetPeriod);

    return [
      ...(selected ? [selected] : []),
      ...this.previousTargetPeriods().filter(({ businessId }) => businessId !== selectedTargetPeriod),
    ];
  });

  protected readonly excludedAccounts = toSignal(
    toObservable(this.selectedTargetPeriod).pipe(
      switchMap((targetPeriod) =>
        targetPeriod
          ? this.buyOutAndSurplusInfoService.getExcludedAccountsForBuyOutSurplusRun(targetPeriod)
          : of<TargetUnitAccountBusinessInfoDTO[]>([]),
      ),
    ),
    { initialValue: [] as TargetUnitAccountBusinessInfoDTO[] },
  );

  protected readonly excludedAccountsColumns: GovukTableColumn[] = [
    { field: 'businessId', header: 'TU ID' },
    { field: 'name', header: 'Operator name' },
    { field: 'status', header: 'Status', widthClass: 'govuk-!-width-one-quarter' },
  ];

  protected readonly buyOutCostsColumns: GovukTableColumn<TargetPeriodBuyOutDetails>[] = [
    { field: 'businessId', header: 'Target period ID', isHeader: true, widthClass: 'govuk-!-width-one-third' },
    { field: 'buyOutCost', header: 'Buy-out cost' },
  ];

  protected readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);

  onCreateNewBatch() {
    const targetPeriodType = this.selectedTargetPeriod();

    // nothing to run against, the submit button is hidden in this state
    if (!targetPeriodType) return;

    // the batch cannot price a buy-out fee without every listed cost, and the API cannot say which one is missing
    const missingCost = this.buyOutCosts().find(({ buyOutCost }) => buyOutCost === null || buyOutCost === undefined);

    if (missingCost) {
      this.errorSummaryInfo.set({
        message: `You must first set the ${missingCost.businessId} cost in order for the batch run to proceed.`,
        link: `/buyout-surplus/cost/${missingCost.businessId}`,
        linkText: `Set the ${missingCost.businessId} buy-out cost`,
      });

      return;
    }

    this.errorSummaryInfo.set(null);

    const { requestType, payloadType } = buyOutRunCreateActionFor(targetPeriodType);

    this.requestsService
      .processRequestCreateAction(
        {
          requestType,
          requestCreateActionPayload: {
            payloadType,
            targetPeriodType,
          } as BuyOutSurplusFacilityRunCreateActionPayload,
        },
        'ENGLAND',
      )
      .subscribe({
        next: (res) => {
          this.router.navigate(['..', 'confirmation'], {
            relativeTo: this.activatedRoute,
            replaceUrl: true,
            queryParams: { referenceCode: res.requestId },
          });
        },
        error: (error) => {
          const errorData = error.error.data[0];
          const errorCode = errorData.requests && errorData.valid === false ? 'inProgress' : 'unavailable';

          this.router.navigate(['..', 'request-error'], {
            relativeTo: this.activatedRoute,
            queryParams: { errorCode },
          });
        },
      });
  }
}
