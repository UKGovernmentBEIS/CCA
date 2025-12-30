import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { DaysRemainingPipe, ItemLinkPipe, ItemNamePipe } from '@netz/common/pipes';

import { ItemDTO } from 'cca-api';

/**
 * Marked for refactor
 * Split floats to grid
 */
@Component({
  selector: 'netz-related-tasks',
  templateUrl: './related-tasks.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, ItemNamePipe, ItemLinkPipe, DaysRemainingPipe],
})
export class RelatedTasksComponent {
  protected readonly items = input<ItemDTO[]>();
  protected readonly heading = input<string>('Related tasks');
  protected readonly noBorders = input<boolean>(false);
}
