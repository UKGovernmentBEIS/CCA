import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';

import { ItemDTO } from 'cca-api';

/**
 * Marked for refactor
 * Split floats to grid
 */
@Component({
  selector: 'cca-related-tasks',
  standalone: true,
  templateUrl: './related-tasks.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgIf, NgFor, RouterLink, ItemNamePipe, ItemLinkPipe],
})
export class RelatedTasksComponent {
  @Input() items: ItemDTO[];
  @Input() heading = 'Related tasks';
  @Input() noBorders = false;

  constructor(public readonly router: Router) {}
}
