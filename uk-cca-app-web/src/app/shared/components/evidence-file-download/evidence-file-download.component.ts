import { AsyncPipe } from '@angular/common';
import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, inject, viewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { combineLatest, expand, map, Observable, of, switchMap, timer } from 'rxjs';

import {
  BuyOutAndSurplusTransactionInfoViewService,
  FileEvidencesService,
  FileToken,
  SubsistenceFeesMoAReceivedAmountControllerService,
} from 'cca-api';

type EvidenceFileDownloadInfo = {
  request: Observable<FileToken>;
  fileType: 'attachment' | 'document';
};

@Component({
  selector: 'cca-evidence-file-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p>You should see your downloads in the downloads folder.</p>
    <a class="govuk-link" [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  standalone: true,
  imports: [AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EvidenceFileDownloadComponent implements AfterViewChecked {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fileEvidencesService = inject(FileEvidencesService);
  private readonly subsistenceFeesMoAReceivedAmountControllerService = inject(
    SubsistenceFeesMoAReceivedAmountControllerService,
  );
  private readonly buyOutAndSurplusTransactionInfoViewService = inject(BuyOutAndSurplusTransactionInfoViewService);

  protected readonly anchor = viewChild<ElementRef<HTMLAnchorElement>>('anchor');

  private hasDownloadedOnce = false;
  private fileDownloadAttachmentPath = `${this.fileEvidencesService.configuration.basePath}/v1.0/file-evidences/`;

  readonly url$ = this.activatedRoute.paramMap.pipe(
    map((params): EvidenceFileDownloadInfo => {
      if (params.has('moaId')) return this.subsistenceFeesMoasDownloadInfo(params);
      if (params.has('transactionId')) return this.buyoutAndSurplusDownloadInfo(params);
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

  private subsistenceFeesMoasDownloadInfo(params: ParamMap): EvidenceFileDownloadInfo {
    return {
      request: this.subsistenceFeesMoAReceivedAmountControllerService.generateGetMoaReceivedAmountEvidenceFileToken(
        +params.get('moaId'),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }

  private buyoutAndSurplusDownloadInfo(params: ParamMap): EvidenceFileDownloadInfo {
    return {
      request: this.buyOutAndSurplusTransactionInfoViewService.generateBuyOutSurplusTransactionEvidenceFileToken(
        +params.get('transactionId'),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }
}
