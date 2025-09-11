import { ChangeDetectionStrategy, Component, computed, effect, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { GovukSelectOption } from '@netz/govuk-components';

@Component({
  selector: 'cca-pagination',
  templateUrl: './pagination.component.html',
  styles: `
    .pagination-items-row {
      align-items: baseline;
      margin-left: auto !important;
    }
  `,
  standalone: true,
  imports: [ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaginationComponent {
  readonly count = input.required<number>();
  readonly hideNumbers = input(false);
  readonly pageSize = input.required<number>();
  readonly currentPage = input.required<number>();

  readonly pageChange = output<number>();
  readonly pageSizeChange = output<number>();

  readonly pageSizeNumbers: GovukSelectOption[] = [
    { text: '10', value: 10 },
    { text: '20', value: 20 },
    { text: '30', value: 30 },
    { text: '40', value: 40 },
    { text: '50', value: 50 },
    { text: '100', value: 100 },
  ];

  readonly totalPages = computed(() => Math.ceil((this.count() || 1) / (this.pageSize() || 1)));

  readonly pageNumbers = computed(() =>
    Array(this.totalPages())
      .fill(0)
      .map((_, i) => i + 1),
  );

  readonly pageSizeForm = new FormGroup({
    pageSize: new FormControl<number>(null),
  });

  constructor() {
    effect(() => {
      this.pageSizeForm.patchValue({ pageSize: this.pageSize() });
    });
  }

  onPageChange(page: number): void {
    if (page < 1 || page > this.totalPages()) return;
    this.pageChange.emit(page);
  }

  onPreviousPage(): void {
    if (this.currentPage() > 1) this.onPageChange(this.currentPage() - 1);
  }

  onNextPage(): void {
    if (this.currentPage() < this.totalPages()) this.onPageChange(this.currentPage() + 1);
  }

  onPageSizeChange(): void {
    const newPageSize = Number(this.pageSizeForm.value.pageSize);
    this.pageSizeChange.emit(newPageSize);
  }

  isDisplayed(target: number, current: number): boolean {
    return this.pageNumbers().length <= 6 || Math.abs(target - current) <= 1;
  }

  isDots(target: number, current: number): boolean {
    return (
      this.pageNumbers().length > 6 && target !== 1 && target !== this.totalPages() && Math.abs(target - current) === 2
    );
  }

  getStartingItem(): number {
    return this.count() === 0 ? 0 : (this.currentPage() - 1) * this.pageSize() + 1;
  }

  getEndingItem(): number {
    if (this.count() === 0) return 0;
    return Math.min(this.currentPage() * this.pageSize(), this.count());
  }
}
