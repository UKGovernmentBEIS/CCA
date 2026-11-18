import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { TaskListComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';

import { underlyingAgreementMigratedQuery } from '../../underlying-agreement-migrated/+state/underlying-agreement-migrated.selectors';
import { getAllUnderlyingAgreementSections } from '../underlying-agreement-submitted-task-content';

@Component({
  selector: 'cca-underlying-agreement-submitted-task-list',
  template: `<netz-task-list [sections]="sections()" />`,
  imports: [TaskListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementSubmittedTaskListComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly sections = computed(() =>
    getAllUnderlyingAgreementSections(
      this.requestActionStore.select(underlyingAgreementMigratedQuery.selectPayload)()?.underlyingAgreement,
    ),
  );
}
