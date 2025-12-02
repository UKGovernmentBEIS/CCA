import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { WarningTextComponent } from '@netz/govuk-components';

import { AccountNotesService } from 'cca-api';

@Component({
  selector: 'cca-delete-note',
  template: `
    <div>
      <netz-page-heading>Are you sure you want to delete this note?</netz-page-heading>
      <p>Your note will be deleted permanently.</p>
      <govuk-warning-text assistiveText="">You will not be able to undo this action.</govuk-warning-text>
      <button (click)="onDelete()" class="govuk-button govuk-button--warning">Delete note</button>
    </div>
    <a routerLink="../../../" fragment="notes" class="govuk-link">Return to: Target unit notes</a>
  `,
  imports: [WarningTextComponent, PageHeadingComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteNoteComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly accountNotesService = inject(AccountNotesService);

  private readonly noteId = +this.activatedRoute.snapshot.paramMap.get('noteId');

  onDelete() {
    if (!this.noteId) return;
    this.accountNotesService.deleteAccountNote(this.noteId).subscribe(() => {
      this.router.navigate(['../../../'], {
        relativeTo: this.activatedRoute,
        fragment: 'notes',
        queryParamsHandling: 'merge',
      });
    });
  }
}
