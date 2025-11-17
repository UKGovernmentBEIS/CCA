import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { WarningTextComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, underlyingAgreementQuery } from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../../transform';
import { extractReviewProps } from '../../../../../utils';
import { updateVariableEnergyProductStatus } from '../product-status.helpers';

@Component({
  selector: 'cca-exclude-product',
  template: `
    <netz-page-heading [caption]="facility()?.facilityDetails?.name">
      Are you sure you want to exclude {{ productName }}?
    </netz-page-heading>

    <p>Your product and all its data will remain available but will be marked as excluded.</p>

    <govuk-warning-text assistiveText=""
      >The product will be treated as excluded and removed from all baseline calculations.</govuk-warning-text
    >

    <button (click)="onExclude()" class="govuk-button govuk-button--warning">Exclude product</button>
  `,
  imports: [WarningTextComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExcludeProductComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly productName = this.activatedRoute.snapshot.paramMap.get('productName');

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onExclude() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const updatedPayload = updateVariableEnergyProductStatus(
      actionPayload,
      this.productName,
      this.facilityId,
      'EXCLUDED',
    );

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
    });
  }
}
