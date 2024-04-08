export abstract class BaseWizardStepContainerComponent {
  protected abstract submit(): void;

  protected abstract get subtask(): string;
  protected abstract get step(): string;
}
