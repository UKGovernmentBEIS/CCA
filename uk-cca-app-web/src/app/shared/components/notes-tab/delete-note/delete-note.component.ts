import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Observable } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { WarningTextComponent } from '@netz/govuk-components';

import { AccountNotesService, RequestNotesService } from 'cca-api';

@Component({
  selector: 'cca-workflow-delete-note',
  template: `
    <div>
      <netz-page-heading>Are you sure you want to delete this note?</netz-page-heading>
      <p>Your note will be deleted permanently.</p>
      <govuk-warning-text assistiveText="">You will not be able to undo this action.</govuk-warning-text>
      <button (click)="onDelete()" class="govuk-button govuk-button--warning">Delete note</button>
    </div>
    <a routerLink="../../../" fragment="notes" class="govuk-link">Return to: Workflow notes</a>
  `,
  imports: [WarningTextComponent, PageHeadingComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowDeleteNoteComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestNotesService = inject(RequestNotesService);
  private readonly accountNotesService = inject(AccountNotesService);

  private readonly noteId = +this.activatedRoute.snapshot.paramMap.get('noteId');
  protected readonly workflowId = this.activatedRoute.snapshot.paramMap.get('workflowId');

  onDelete() {
    if (!this.noteId) return;

    const request$: Observable<unknown> = this.workflowId
      ? this.requestNotesService.deleteRequestNote(this.noteId)
      : this.accountNotesService.deleteAccountNote(this.noteId);

    request$.subscribe(() => {
      this.router.navigate(['../../../'], {
        relativeTo: this.activatedRoute,
        fragment: 'notes',
        queryParamsHandling: 'merge',
      });
    });
  }
}
