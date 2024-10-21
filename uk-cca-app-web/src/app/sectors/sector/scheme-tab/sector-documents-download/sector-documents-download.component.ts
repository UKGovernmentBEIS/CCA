import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { switchMap, tap, timer } from 'rxjs';

import { Configuration, FileToken, SectorAssociationSchemeService } from 'cca-api';

@Component({
  selector: 'cca-sector-documents-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="downloadURL()" download #anchor>Click to restart download if it fails</a>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorDocumentsDownloadComponent {
  private readonly anchor = viewChild<ElementRef>('anchor');

  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  private readonly route = inject(ActivatedRoute);
  private readonly configuration = inject(Configuration);

  private readonly sectorId: number = +this.route.snapshot.paramMap.get('sectorId');
  private readonly uuid: string = this.route.snapshot.paramMap.get('uuid');
  private readonly basePath: string = `${this.configuration.basePath}/v1.0/sector-documents/document/`;

  private hasDownloadedOnce = false;
  private token = signal('');

  private getToken = this.sectorAssociationSchemeService
    .generateGetSectorAssociationSchemeDocumentToken(this.sectorId, this.uuid)
    .pipe(tap(({ token }) => this.token.set(token)));

  downloadURL = computed(() => (this.token() ? `${this.basePath}${this.token()}` : ''));

  constructor() {
    this.getToken
      .pipe(
        switchMap((fileToken: FileToken) => {
          return timer(0, fileToken.tokenExpirationMinutes * 1000 * 60).pipe(switchMap(() => this.getToken));
        }),
      )
      .subscribe();

    effect(() => {
      if (this.downloadURL() && !this.hasDownloadedOnce) {
        // we have to setTimeout in order to give angular the chance to update the dom with the new URL
        setTimeout(() => {
          this.anchor().nativeElement.click();
          this.hasDownloadedOnce = true;
          onfocus = () => close();
        }, 0);
      }
    });
  }
}
