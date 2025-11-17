import { ChangeDetectionStrategy, Component, computed, effect, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EMPTY, switchMap, tap } from 'rxjs';

import {
  GovukSelectOption,
  SelectComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { PerformanceDataTargetPeriodEnum } from '@requests/common';

import {
  AccountPerformanceAccountTemplateDataReportInfoDTO,
  TargetPeriodPerformanceAccountTemplateDataReportOfTheAccountService,
} from 'cca-api';

import { PatReportStore } from '../../pat-report-store';

@Component({
  selector: 'cca-pat-report',
  templateUrl: './pat-report.component.html',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    SelectComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatReportComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly patReportStore = inject(PatReportStore);
  private readonly targetPeriodPerformanceAccountTemplateDataReportOfTheAccountService = inject(
    TargetPeriodPerformanceAccountTemplateDataReportOfTheAccountService,
  );

  protected readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');
  protected readonly targetPeriodsOptions: GovukSelectOption<PerformanceDataTargetPeriodEnum.TP6>[] = [
    {
      value: PerformanceDataTargetPeriodEnum.TP6,
      text: PerformanceDataTargetPeriodEnum.TP6,
    },
  ];

  protected readonly form = new FormGroup({
    targetPeriodType: new FormControl<PerformanceDataTargetPeriodEnum>(PerformanceDataTargetPeriodEnum.TP6),
  });

  protected readonly targetPeriodValue = toSignal(this.form.controls.targetPeriodType.valueChanges, {
    initialValue: this.form.controls.targetPeriodType.value,
  });

  protected readonly reportInfoData: Signal<AccountPerformanceAccountTemplateDataReportInfoDTO | null> = computed(() =>
    this.patReportStore.stateAsSignal()?.reportInfo?.targetPeriodType
      ? this.patReportStore.stateAsSignal()?.reportInfo
      : null,
  );

  constructor() {
    effect(() => {
      const selectedValue = this.targetPeriodValue();

      if (selectedValue) {
        this.fetchPatReportInfoDTO(selectedValue)
          .pipe(
            switchMap((reportInfo) => {
              this.patReportStore.setState({ reportInfo: reportInfo, reportDetails: null });
              if (reportInfo?.targetPeriodName) {
                return this.fetchPatReportSubmittedData(selectedValue).pipe(
                  tap((reportDetails) => {
                    this.patReportStore.setState({ reportInfo, reportDetails });
                  }),
                );
              }
              return EMPTY;
            }),
          )
          .subscribe();
      }
    });
  }

  fetchPatReportInfoDTO(targetPeriodType: PerformanceDataTargetPeriodEnum) {
    return this.targetPeriodPerformanceAccountTemplateDataReportOfTheAccountService.getAccountPerformanceAccountTemplateDataReportInfo(
      this.targetUnitId,
      targetPeriodType,
    );
  }

  fetchPatReportSubmittedData(targetPeriodType: PerformanceDataTargetPeriodEnum) {
    return this.targetPeriodPerformanceAccountTemplateDataReportOfTheAccountService.getAccountPerformanceAccountTemplateDataReportDetails(
      this.targetUnitId,
      targetPeriodType,
    );
  }
}
