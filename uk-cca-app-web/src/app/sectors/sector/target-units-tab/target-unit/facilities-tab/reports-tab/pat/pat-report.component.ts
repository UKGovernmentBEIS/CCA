import { DatePipe } from '@angular/common';
import { Component, computed, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { of, switchMap, take, tap } from 'rxjs';

import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { FacilityPatReportingTypesPipe } from '@shared/pipes';
import { SpreadsheetExportService } from '@shared/services';

import { FacilityInfoViewService, TargetPeriodPerformanceAccountTemplateDataReportOfTheFacilityService } from 'cca-api';

import { FacilityPATReportStore, ReportingYear } from '../../facility-pat-report.store';
import { toFacilityPATReportsSummaryData } from '../facility-reports-summary-data';

@Component({
  selector: 'cca-pat-report',
  templateUrl: './pat-report.component.html',
  imports: [ReactiveFormsModule, SelectComponent, SummaryComponent, RouterLink],
})
export class PatReportComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityPATReportStore = inject(FacilityPATReportStore);
  private readonly facilityInfoViewService = inject(FacilityInfoViewService);
  private readonly spreadsheetExportService = inject(SpreadsheetExportService);

  private readonly targetPeriodPerformanceAccountTemplateDataReportOfTheFacilityService = inject(
    TargetPeriodPerformanceAccountTemplateDataReportOfTheFacilityService,
  );

  protected readonly facilityId = +this.activatedRoute.snapshot.paramMap.get('facilityId');

  private readonly facilityDetails = toSignal(this.facilityInfoViewService.getFacilityDetailsById(this.facilityId));

  private readonly state = this.facilityPATReportStore.stateAsSignal;

  readonly form = new FormGroup({
    reportingYear: new FormControl<ReportingYear | null>(this.state().reportingYear ?? null),
  });

  protected readonly reportingYearOptions: GovukSelectOption<ReportingYear>[] = [
    { value: '2026', text: '2026' },
    { value: '2027', text: '2027' },
    { value: '2028', text: '2028' },
    { value: '2029', text: '2029' },
    { value: '2030', text: '2030' },
  ];

  protected readonly reportingYearValue = toSignal(this.form.controls.reportingYear.valueChanges, {
    initialValue: this.form.controls.reportingYear.value,
  });

  protected readonly summaryData = computed(() => {
    const reportInfo = this.state().reportInfo;
    return reportInfo ? toFacilityPATReportsSummaryData(reportInfo) : null;
  });

  constructor() {
    effect(() => {
      const reportingYear = this.reportingYearValue();

      if (reportingYear) {
        this.fetchAccountPerformanceData(this.facilityId, reportingYear);
      }
    });
  }

  exportToCSV() {
    const actions = this.state().details?.data?.savingActions ?? [];

    const typesPipe = new FacilityPatReportingTypesPipe();
    const datePipe = new DatePipe('en-GB');

    const data = actions.map((action) => ({
      'Facility Identifier': this.facilityDetails()?.facilityBusinessId ?? '',
      Category: typesPipe.transform(action.actionCategoryType),
      'Supply/Demand side measure': typesPipe.transform(action.supplyDemandSideMeasure),
      'Savings Actions Implemented': action.savingActionsImplemented,
      'Reasons for Implementation': action.reasonsForImplementation,
      'Implementation Date': datePipe.transform(action.implementationDate, 'dd/MM/yyyy'),
      'Fixed Energy Consumption or Carbon Emissions Impacted?': typesPipe.transform(
        action.fixedEnergyConsumptionOrCarbonEmissionsImpacted,
      ),
      'Energy Consumption or Carbon Emissions Impacted (%)':
        action.energyConsumptionOrCarbonEmissionsImpactedPercentage,
      'Expected extent (penetration) of the change implemented (%)': action.expectedExtentOfChangeImplementedPercentage,
      'Expected % savings from the change implemented (%)': action.expectedSavingsFromTheChangeImplementedPercentage,
      Notes: action.notes,
    }));

    this.spreadsheetExportService.exportToCsv(data, 'pat-report.csv', 'PAT report');
  }

  private fetchAccountPerformanceData(facilityId: number, reportingYear: ReportingYear) {
    return this.targetPeriodPerformanceAccountTemplateDataReportOfTheFacilityService
      .getFacilityPerformanceAccountTemplateDataReportInfo(facilityId, reportingYear)
      .pipe(
        take(1),
        tap((reportInfo) => this.facilityPATReportStore.updateState({ reportInfo, reportingYear })),
        switchMap((reportInfo) => {
          if (!reportInfo) return of(null);

          return this.targetPeriodPerformanceAccountTemplateDataReportOfTheFacilityService.getFacilityPerformanceAccountTemplateDataReportDetails(
            facilityId,
            reportInfo.targetPeriodYear.toString(),
          );
        }),
        tap((details) => this.facilityPATReportStore.updateState({ details })),
      )
      .subscribe();
  }
}
