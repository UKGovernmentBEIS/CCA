import { Directive, ElementRef, HostBinding, inject, input, OnChanges, Renderer2, SimpleChanges } from '@angular/core';

@Directive({
  selector: '[ccaProgressBar]',
})
export class ProgressBarDirective implements OnChanges {
  private readonly renderer = inject(Renderer2);
  private readonly el = inject(ElementRef<HTMLDivElement>);

  protected readonly passwordStrength = input.required<number>();
  protected readonly numberOfProgressBarItems = input(5);
  protected readonly colors = input<string[]>();

  protected readonly progressBar = this.el.nativeElement;

  @HostBinding('attr.aria-valuemin') minProgressVal = 0;
  @HostBinding('attr.aria-valuemax') maxProgressVal = 100;
  @HostBinding('attr.aria-valuenow') currentProgressVal = 0;
  @HostBinding('attr.data-strength') dataPasswordStrength = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.numberOfProgressBarItems) this.setProgressBarItems();
    this.setProgressBar();
  }

  setProgressBarItems(): void {
    const progressBarItemContainer = this.progressBar.querySelector('.psm__progress-bar-items') as HTMLDivElement;
    if (!progressBarItemContainer) return;

    const itemCount = Math.max(1, this.numberOfProgressBarItems());
    const width = 100 / itemCount;

    while (progressBarItemContainer.firstChild) {
      this.renderer.removeChild(progressBarItemContainer, progressBarItemContainer.firstChild);
    }

    for (let i = 0; i < itemCount; i++) {
      const progressBarItem = this.renderer.createElement('div') as HTMLDivElement;
      this.renderer.addClass(progressBarItem, 'psm__progress-bar-item');
      this.renderer.setStyle(progressBarItem, 'width', `${width}%`);
      this.renderer.appendChild(progressBarItemContainer, progressBarItem);
    }
  }

  setProgressBar(): void {
    const progressBarOverlayWidth = this.getFillMeterWidth(this.passwordStrength());
    const progressBarOverlayWidthInPx = `${progressBarOverlayWidth}%`;

    const progressLevelBasedOnItems = (progressBarOverlayWidth / 100) * Math.max(1, this.numberOfProgressBarItems());
    const progressBarOverlayColor = this.getMeterFillColor(progressLevelBasedOnItems);

    this.dataPasswordStrength = this.passwordStrength() ?? 0;
    this.currentProgressVal = progressBarOverlayWidth;

    const overlayElement = this.progressBar.querySelector('.psm__progress-bar-overlay');

    if (overlayElement) {
      this.renderer.setStyle(overlayElement, 'width', progressBarOverlayWidthInPx);
      this.renderer.setStyle(overlayElement, 'background-color', progressBarOverlayColor);
    }
  }

  getFillMeterWidth(strength: number | null | undefined): number {
    if (strength === null || strength === undefined) return 0;

    const strengthInPercentage = strength !== null ? ((strength + 1) / 5) * 100 : 0;

    const roundedStrengthInPercentage = this.getRoundedStrength(
      strengthInPercentage,
      100 / this.numberOfProgressBarItems(),
    );

    return roundedStrengthInPercentage;
  }

  getMeterFillColor(progressLevel: number): string {
    const cols = this.colors() ?? [];
    if (cols.length === 0) return 'transparent';

    if (!progressLevel || progressLevel <= 0) {
      return cols[0];
    }

    const index = Math.min(Math.max(Math.ceil(progressLevel) - 1, 0), cols.length - 1);
    return cols[index];
  }

  private getRoundedStrength(strength: number, roundTo: number): number {
    return Math.round(strength / roundTo) * roundTo;
  }
}
