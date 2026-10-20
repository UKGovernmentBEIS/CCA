import { inject } from '@angular/core';

import { TaskService } from '../services';

export abstract class BaseWizardStepContainerComponent {
  protected taskService = inject(TaskService);
  protected abstract submit(): void;

  protected abstract get subtask(): string;
  protected abstract get step(): string;
}
