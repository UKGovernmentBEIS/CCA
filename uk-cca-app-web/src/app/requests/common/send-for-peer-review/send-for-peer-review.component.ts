import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { catchError, throwError } from 'rxjs';

import { GovukValidators, SelectComponent } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { AssigneeUserInfoDTO, PeerReviewRequestTaskActionPayload, TasksService } from 'cca-api';

@Component({
  selector: 'cca-send-for-peer-review',
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Send for peer review"
      heading="Select peer reviewer"
      data-testid="send-for-peer-review-form"
      submitText="Confirm and complete"
    >
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-one-half">
          <div
            [options]="assigneeOptions"
            formControlName="assigneeUserInfoId"
            label="Select a peer reviewer"
            govuk-select
            widthClass="govuk-!-width-full"
          ></div>
        </div>
      </div>
    </cca-wizard-step>
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" routerLink="../../" [replaceUrl]="true"> {{ returnToText() }} </a>
  `,
  imports: [ReactiveFormsModule, WizardStepComponent, RouterLink, SelectComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendForPeerReviewComponent {
  protected readonly requestTaskActionType = input.required<string>();
  protected readonly payloadType = input.required<string>();
  protected readonly confirmationRoute = input<string>('confirmation');
  protected readonly returnToText = input<string>();

  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksService = inject(TasksService);
  private readonly formBuilder = inject(FormBuilder);

  private readonly taskId = +this.activatedRoute.snapshot.params.taskId;

  protected readonly candidateAssignees = this.activatedRoute.snapshot.data.candidateAssignees as AssigneeUserInfoDTO[];

  protected readonly assigneeOptions = this.candidateAssignees?.length
    ? this.candidateAssignees.map((assignee) => ({
        text: `${assignee.firstName} ${assignee.lastName}`.trim(),
        value: assignee.id,
      }))
    : [];

  protected readonly form = this.formBuilder.group({
    assigneeUserInfoId: ['', GovukValidators.required('Please select an option')],
  });
  onSubmit() {
    if (this.form.valid) {
      const selectedAssigneeId = this.form.value.assigneeUserInfoId;

      this.tasksService
        .processRequestTaskAction({
          requestTaskActionType: this.requestTaskActionType(),
          requestTaskActionPayload: {
            payloadType: this.payloadType(),
            peerReviewer: selectedAssigneeId,
          } as PeerReviewRequestTaskActionPayload,
          requestTaskId: this.taskId,
        })
        .pipe(
          catchError((error) => {
            console.error('Error processing request task action:', error);
            return throwError(() => error);
          }),
        )
        .subscribe(() => {
          this.router.navigate([this.confirmationRoute(), selectedAssigneeId], {
            relativeTo: this.activatedRoute,
          });
        });
    }
  }
}
