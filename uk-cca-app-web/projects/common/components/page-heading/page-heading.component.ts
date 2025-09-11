import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'netz-page-heading',
  standalone: true,
  template: `
    <span [class]="'govuk-caption-' + size" *ngIf="caption">{{ caption }}</span>
    <h1 [class]="'govuk-heading-' + size">
      <ng-content />
    </h1>
  `,
  imports: [NgIf],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageHeadingComponent {
  @Input() caption: string;
  @Input() size: 'l' | 'xl' = 'l';
}
