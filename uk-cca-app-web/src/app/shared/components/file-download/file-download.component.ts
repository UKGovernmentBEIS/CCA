import { AsyncPipe } from '@angular/common';
import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, inject, viewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { combineLatest, expand, map, Observable, of, switchMap, timer } from 'rxjs';

import { LinkDirective } from '@netz/govuk-components';

import {
  FileAttachmentsService,
  FileDocumentsService,
  FileToken,
  RequestActionAttachmentsHandlingService,
  RequestActionFileDocumentsHandlingService,
  RequestTaskAttachmentsHandlingService,
  UnderlyingAgreementsService,
} from 'cca-api';

export type FileDownloadInfo = {
  request: Observable<FileToken>;
  fileType: 'attachment' | 'document';
};

@Component({
  selector: 'cca-file-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  standalone: true,
  imports: [AsyncPipe, LinkDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FileDownloadComponent implements AfterViewChecked {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskAttachmentsHandlingService = inject(RequestTaskAttachmentsHandlingService);
  private readonly requestActionAttachmentsHandlingService = inject(RequestActionAttachmentsHandlingService);
  private readonly requestActionFileDocumentsHandlingService = inject(RequestActionFileDocumentsHandlingService);
  private readonly fileAttachmentsService = inject(FileAttachmentsService);
  private readonly fileDocumentsService = inject(FileDocumentsService);
  private readonly underlyingAgreementsService = inject(UnderlyingAgreementsService);

  anchor = viewChild<ElementRef<HTMLAnchorElement>>('anchor');

  private hasDownloadedOnce = false;
  private fileDownloadAttachmentPath = `${this.fileAttachmentsService.configuration.basePath}/v1.0/file-attachments/`;
  private fileDownloadDocumentPath = `${this.fileDocumentsService.configuration.basePath}/v1.0/file-documents/`;

  url$ = this.activatedRoute.paramMap.pipe(
    map((params): FileDownloadInfo => {
      return params.has('actionId')
        ? this.requestActionDownloadInfo(params)
        : params.has('taskId')
          ? this.requestTaskDownloadInfo(params)
          : this.underlyingAgreementDownloadInfo(params);
    }),
    switchMap(({ request, fileType }) => {
      return combineLatest([
        of(fileType),
        request.pipe(
          expand((response) => timer(response.tokenExpirationMinutes * 1000 * 60).pipe(switchMap(() => request))),
        ),
      ]);
    }),
    map(([fileType, fileToken]) => {
      return fileType === 'attachment'
        ? `${this.fileDownloadAttachmentPath}${encodeURIComponent(String(fileToken.token))}`
        : `${this.fileDownloadDocumentPath}${encodeURIComponent(String(fileToken.token))}`;
    }),
  );

  ngAfterViewChecked(): void {
    if (
      (this.anchor().nativeElement.href.includes(this.fileDownloadAttachmentPath) ||
        this.anchor().nativeElement.href.includes(this.fileDownloadDocumentPath)) &&
      !this.hasDownloadedOnce
    ) {
      this.anchor().nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private requestTaskDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentToken(
        Number(params.get('taskId')),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }

  private requestActionDownloadInfo(params: ParamMap): FileDownloadInfo {
    if (params.get('fileType') === 'document') {
      return {
        request: this.requestActionFileDocumentsHandlingService.generateRequestActionGetFileDocumentToken(
          Number(params.get('actionId')),
          params.get('uuid'),
        ),
        fileType: 'document',
      };
    } else {
      return {
        request: this.requestActionAttachmentsHandlingService.generateRequestActionGetFileAttachmentToken(
          Number(params.get('actionId')),
          params.get('uuid'),
        ),
        fileType: 'attachment',
      };
    }
  }

  private underlyingAgreementDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.underlyingAgreementsService.generateGetUnderlyingAgreementDocumentToken(
        +params.get('unaId'),
        params.get('uuid'),
      ),
      fileType: 'document',
    };
  }
}
