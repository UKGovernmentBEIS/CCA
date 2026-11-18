import { AsyncPipe } from '@angular/common';
import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, inject, viewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { combineLatest, expand, map, Observable, of, switchMap, timer } from 'rxjs';

import {
  BuyOutAndSurplusTransactionInfoViewService,
  FileAttachmentsService,
  FileDocumentsService,
  FileToken,
  RequestActionAttachmentsHandlingService,
  RequestActionFileDocumentsHandlingService,
  RequestTaskAttachmentsHandlingService,
  SubsistenceFeesMoAReceivedAmountControllerService,
  SubsistenceFeesMoAViewService,
  TargetPeriodPerformanceAccountTemplateDataReportOfTheAccountService,
  TargetPeriodPerformanceDataReportOfTheAccountService,
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
    <p>You should see your downloads in the downloads folder.</p>
    <a class="govuk-link" [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  imports: [AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileDownloadComponent implements AfterViewChecked {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskAttachmentsHandlingService = inject(RequestTaskAttachmentsHandlingService);
  private readonly requestActionAttachmentsHandlingService = inject(RequestActionAttachmentsHandlingService);
  private readonly requestActionFileDocumentsHandlingService = inject(RequestActionFileDocumentsHandlingService);
  private readonly subsistenceFeesMoAViewService = inject(SubsistenceFeesMoAViewService);
  private readonly subsistenceFeesMoAReceivedAmountControllerService = inject(
    SubsistenceFeesMoAReceivedAmountControllerService,
  );
  private readonly fileAttachmentsService = inject(FileAttachmentsService);
  private readonly fileDocumentsService = inject(FileDocumentsService);
  private readonly underlyingAgreementsService = inject(UnderlyingAgreementsService);
  private readonly targetPeriodPerformanceDataReportOfTheAccountService = inject(
    TargetPeriodPerformanceDataReportOfTheAccountService,
  );
  private readonly targetPeriodPerformanceAccountTemplateDataReportOfTheAccountService = inject(
    TargetPeriodPerformanceAccountTemplateDataReportOfTheAccountService,
  );
  private readonly buyOutAndSurplusTransactionInfoViewService = inject(BuyOutAndSurplusTransactionInfoViewService);

  protected readonly anchor = viewChild<ElementRef<HTMLAnchorElement>>('anchor');

  private hasDownloadedOnce = false;
  private fileDownloadAttachmentPath = `${this.fileAttachmentsService.configuration.basePath}/v1.0/file-attachments/`;
  private fileDownloadDocumentPath = `${this.fileDocumentsService.configuration.basePath}/v1.0/file-documents/`;

  readonly url$ = this.activatedRoute.paramMap.pipe(
    map((params): FileDownloadInfo => {
      if (params.has('actionId')) return this.requestActionDownloadInfo(params);

      if (params.has('taskId')) return this.requestTaskDownloadInfo(params);

      if (params.has('targetUnitId') && params.has('targetPeriodType') && params.has('reportType'))
        return this.targetUnitAccountDownloadInfo(params);

      if (params.has('moaId')) return this.subsistenceFeesMoasDownloadInfo(params);

      if (params.has('transactionId')) return this.buyOutAndSurplusDownloadInfo(params);

      return this.underlyingAgreementDownloadInfo(params);
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
    }

    return {
      request: this.requestActionAttachmentsHandlingService.generateRequestActionGetFileAttachmentToken(
        Number(params.get('actionId')),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
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

  private subsistenceFeesMoasDownloadInfo(params: ParamMap): FileDownloadInfo {
    if (params.get('fileType') === 'document') {
      return {
        request: this.subsistenceFeesMoAViewService.generateGetSubsistenceFeesMoaDocumentToken(
          +params.get('moaId'),
          params.get('uuid'),
        ),
        fileType: 'document',
      };
    }

    return {
      request: this.subsistenceFeesMoAReceivedAmountControllerService.generateGetMoaReceivedAmountEvidenceFileToken(
        +params.get('moaId'),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }

  private targetUnitAccountDownloadInfo(params: ParamMap): FileDownloadInfo {
    const targetPeriod = params.get('targetPeriodType');
    const requestedReportType = params.get('reportType');

    if (requestedReportType === 'PERFORMANCE') {
      return {
        request:
          this.targetPeriodPerformanceDataReportOfTheAccountService.generateGetAccountPerformanceDataReportAttachmentToken(
            +params.get('targetUnitId'),
            (targetPeriod as 'TP5') || 'TP6',
            params.get('uuid'),
          ),
        fileType: 'attachment',
      };
    }

    if (requestedReportType === 'PAT') {
      return {
        request:
          this.targetPeriodPerformanceAccountTemplateDataReportOfTheAccountService.generateGetAccountPerformanceAccountTemplateDataReportAttachmentToken(
            +params.get('targetUnitId'),
            (targetPeriod as 'TP5') || 'TP6',
            params.get('uuid'),
          ),
        fileType: 'attachment',
      };
    }
  }

  private buyOutAndSurplusDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.buyOutAndSurplusTransactionInfoViewService.generateBuyOutSurplusTransactionDocumentToken(
        +params.get('transactionId'),
        params.get('uuid'),
      ),
      fileType: 'document',
    };
  }
}
