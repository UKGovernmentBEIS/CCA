import { AsyncPipe } from '@angular/common';
import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, inject, viewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { expand, map, Observable, switchMap, timer } from 'rxjs';

import { AccountNotesService, FileDocumentsService, FileToken } from 'cca-api';

@Component({
  selector: 'cca-note-files-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p>You should see your downloads in the downloads folder.</p>
    <a class="govuk-link" [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  imports: [AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NoteFilesDownloadComponent implements AfterViewChecked {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly accountNotesService = inject(AccountNotesService);
  private readonly fileDocumentsService = inject(FileDocumentsService);

  protected readonly anchor = viewChild<ElementRef<HTMLAnchorElement>>('anchor');

  private hasDownloadedOnce = false;
  private readonly fileNotesDocumentPath = `${this.fileDocumentsService.configuration.basePath}/v1.0/file-notes/`;

  readonly url$ = this.activatedRoute.paramMap.pipe(
    switchMap((params) => {
      const request$ = this.notesDownloadInfo(params);

      return request$.pipe(
        expand((response) => timer(response.tokenExpirationMinutes * 1000 * 60).pipe(switchMap(() => request$))),
        map((fileToken) => `${this.fileNotesDocumentPath}${encodeURIComponent(String(fileToken.token))}`),
      );
    }),
  );

  ngAfterViewChecked(): void {
    if (this.anchor().nativeElement.href.includes(this.fileNotesDocumentPath) && !this.hasDownloadedOnce) {
      this.anchor().nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private notesDownloadInfo(params: ParamMap): Observable<FileToken> {
    return this.accountNotesService.generateGetAccountFileNoteToken(+params.get('targetUnitId'), params.get('uuid'));
  }
}
