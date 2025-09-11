import { AsyncPipe } from '@angular/common';
import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, inject, viewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { combineLatest, expand, map, Observable, of, switchMap, timer } from 'rxjs';

import { DocumentTemplateFilesService, FileDocumentTemplatesService, FileToken } from 'cca-api';

import { FileDownloadInfo } from '../file-download';

type EvidenceFileDownloadInfo = {
  request: Observable<FileToken>;
  fileType: 'attachment' | 'document';
};

@Component({
  selector: 'cca-document-templates-file-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p>You should see your downloads in the downloads folder.</p>
    <a class="govuk-link" [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  standalone: true,
  imports: [AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentTemplatesFileDownloadComponent implements AfterViewChecked {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fileDocumentTemplatesService = inject(FileDocumentTemplatesService);
  private readonly documentTemplateFilesService = inject(DocumentTemplateFilesService);

  protected readonly anchor = viewChild<ElementRef<HTMLAnchorElement>>('anchor');

  private hasDownloadedOnce = false;
  private fileDownloadAttachmentPath = `${this.fileDocumentTemplatesService.configuration.basePath}/v1.0/file-document-templates/`;

  readonly url$ = this.activatedRoute.paramMap.pipe(
    map((params): EvidenceFileDownloadInfo => {
      if (params.has('templateId')) return this.documentTEmplatesDownloadInfo(params);
    }),
    switchMap(({ request, fileType }) =>
      combineLatest([
        of(fileType),
        request.pipe(
          expand((response) => timer(response.tokenExpirationMinutes * 1000 * 60).pipe(switchMap(() => request))),
        ),
      ]),
    ),
    map(([fileType, fileToken]) => {
      if (fileType === 'attachment') {
        return `${this.fileDownloadAttachmentPath}${encodeURIComponent(String(fileToken.token))}`;
      }

      throw new Error('Invalid file type. Document is not supported yet.');
    }),
  );

  ngAfterViewChecked(): void {
    if (this.anchor().nativeElement.href.includes(this.fileDownloadAttachmentPath) && !this.hasDownloadedOnce) {
      this.anchor().nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private documentTEmplatesDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.documentTemplateFilesService.generateGetDocumentTemplateFileToken(
        +params.get('templateId'),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }
}
