import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { RequestInfoDTO, RequestMetadata, RequestTaskDTO } from 'cca-api';

import { getHeadingMap } from '../../../core/payment.map';

@Component({
  selector: 'cca-return-link',
  template: ` <a govukLink [routerLink]="link"> Return to: {{ linkName }} </a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent implements OnInit {
  @Input() home = false;
  @Input() requestType: RequestInfoDTO['type'];
  @Input() requestMetadata: RequestMetadata;
  @Input() requestTaskType: RequestTaskDTO['type'];
  @Input() returnLink: string;

  link: string;
  linkName: string;

  ngOnInit(): void {
    if (this.home) {
      this.linkName = 'Dashboard';
      this.link = '/dashboard';
    } else {
      const headingMap = getHeadingMap((this.requestMetadata as any)?.year);
      this.linkName = headingMap[this.requestTaskType];
      this.link = this.returnLink;
    }
  }
}
