/* eslint-disable @angular-eslint/sort-ngmodule-metadata-arrays */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { StatusTagColorPipe } from '@common/request-task/pipes/status-tag/status-tag-color/status-tag-color.pipe';
import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { SelectOtherComponent } from '@shared/components/select-other/select-other.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PipesModule } from '@shared/pipes/pipes.module';

import { GovukComponentsModule } from 'govuk-components';

import { AddAnotherDirective } from './add-another/add-another.directive';
import { BaseSuccessComponent } from './base-success/base-success.component';
import { BooleanRadioGroupComponent } from './boolean-radio-group/boolean-radio-group.component';
import { SummaryDownloadFilesComponent } from './components/summary-download-files/summary-download-files.component';
import { AsyncValidationFieldDirective } from './directives/async-validation-field.directive';
import { GroupedSummaryListDirective } from './grouped-summary-list/grouped-summary-list.directive';
import { PaginationComponent } from './pagination/pagination.component';
import { PendingButtonDirective } from './pending-button.directive';
import { PhaseBarComponent } from './phase-bar/phase-bar.component';
import { TextEllipsisPipe } from './pipes/text-ellipsis.pipe';
import { SkipLinkFocusDirective } from './skip-link-focus.directive';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    GovukComponentsModule,
    PipesModule,
    ReactiveFormsModule,
    RouterModule,
    StatusTagColorPipe,
    PageHeadingComponent,
    PaginationComponent,
    PendingButtonDirective,
  ],
  declarations: [
    AddAnotherDirective,
    AsyncValidationFieldDirective,
    BaseSuccessComponent,
    BooleanRadioGroupComponent,
    GroupedSummaryListDirective,
    PaymentNotCompletedComponent,
    PhaseBarComponent,
    SelectOtherComponent,
    SkipLinkFocusDirective,
    SummaryDownloadFilesComponent,
  ],
  exports: [
    AddAnotherDirective,
    AsyncValidationFieldDirective,
    BaseSuccessComponent,
    BooleanRadioGroupComponent,
    CommonModule,
    FormsModule,
    GovukComponentsModule,
    GroupedSummaryListDirective,
    PaginationComponent,
    PaymentNotCompletedComponent,
    PhaseBarComponent,
    PipesModule,
    ReactiveFormsModule,
    SelectOtherComponent,
    SkipLinkFocusDirective,
    SummaryDownloadFilesComponent,
    TextEllipsisPipe,
  ],
})
export class SharedModule {}
