import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Input, signal } from '@angular/core';

@Component({
  selector: 'cca-more-less-text',
  standalone: true,
  imports: [NgIf],
  templateUrl: './more-less.component.html',
  styleUrls: ['./more-less.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MoreLessComponent {
  @Input() text: string;
  @Input() index: number;
  @Input() widthClass: string;

  moreIsClicked = signal(false);

  class = computed(() => `${this.widthClass} ${!this.moreIsClicked() ? 'cell-ellipsis' : ''}`);

  moreLessClicked() {
    this.moreIsClicked.update((v) => !v);
  }

  isTextOverflow(elementId: string): boolean {
    const elem = document.getElementById(elementId);

    return elem.offsetWidth < elem.scrollWidth || this.moreIsClicked();
  }
}
