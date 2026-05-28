import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  effect,
  ElementRef,
  forwardRef,
  HostListener,
  inject,
  Injector,
  input,
  OnInit,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ControlContainer,
  ControlValueAccessor,
  FormGroupDirective,
  NG_VALUE_ACCESSOR,
  NgControl,
  NgForm,
  UntypedFormControl,
} from '@angular/forms';

import { ErrorMessageComponent, FormService, GovukSelectOption, GovukTextWidthClass } from '@netz/govuk-components';

let nextComboboxId = 0;

@Component({
  selector: 'cca-combobox',
  templateUrl: './combobox.component.html',
  imports: [NgClass, ErrorMessageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ComboboxComponent),
      multi: true,
    },
  ],
  host: {
    class: 'govuk-!-display-block govuk-form-group',
    '[class.govuk-form-group--error]': 'showErrors()',
  },
  styleUrl: './combobox.css',
})
export class ComboboxComponent implements ControlValueAccessor, OnInit {
  private readonly formService = inject(FormService);
  private readonly container = inject(ControlContainer, { optional: true });
  private readonly destroyRef = inject(DestroyRef);
  private readonly elementRef = inject(ElementRef);
  private readonly injector = inject(Injector);

  private readonly inputEl = viewChild<ElementRef<HTMLInputElement>>('inputEl');

  private ngControl: NgControl | null = null;
  private readonly fallbackIdentifier = `cca-combobox-${nextComboboxId++}`;
  private onChange: (value: string | null) => void = () => undefined;
  private onTouched: () => void = () => undefined;
  private suppressInline = false;

  readonly options = input<GovukSelectOption<string | null>[]>([]);
  readonly widthClass = input<GovukTextWidthClass>(undefined);
  readonly placeholder = input<string>('');

  readonly inputValue = signal('');
  readonly typedQuery = signal('');
  readonly selectedValue = signal<string | null>(null);
  readonly isOpen = signal(false);
  readonly isDisabled = signal(false);
  readonly activeIndex = signal(-1);
  readonly isSubmitted = signal(false);

  get control(): UntypedFormControl | null {
    return (this.ngControl?.control as UntypedFormControl) ?? null;
  }

  get identifier(): string {
    if (!this.ngControl) return this.fallbackIdentifier;

    return this.formService.getControlIdentifier(this.ngControl) ?? this.fallbackIdentifier;
  }

  readonly filteredOptions = computed(() => {
    const query = this.typedQuery().trim().toLowerCase();
    const availableOptions = this.options().filter((option) => !option.disabled);

    if (!query) return availableOptions;

    return availableOptions.filter((option) => option.text.toLowerCase().includes(query));
  });

  readonly activeDescendantId = computed(() => {
    const idx = this.activeIndex();
    return idx >= 0 ? `${this.identifier}-option-${idx}` : null;
  });

  readonly inlineMatch = computed(() => {
    const query = this.typedQuery().trim().toLowerCase();
    if (!query) return null;
    const available = this.options().filter((o) => !o.disabled);
    return available.find((o) => o.text.toLowerCase().startsWith(query)) ?? null;
  });

  constructor() {
    effect(() => {
      const opts = this.options();
      const val = this.selectedValue();
      if (val != null && !this.isOpen()) {
        const found = opts.find((o) => o.value === val);
        this.inputValue.set(found?.text ?? '');
      }
    });
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.container &&
      (this.container.formDirective instanceof FormGroupDirective || this.container.formDirective instanceof NgForm)
      ? this.container.formDirective
      : null;
  }

  showErrors(): boolean {
    return !!this.control?.invalid && (!this.form || this.isSubmitted());
  }

  ngOnInit(): void {
    this.ngControl = this.injector.get(NgControl, null, { self: true, optional: true });

    this.form?.ngSubmit.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.isSubmitted.set(true);
    });
  }

  writeValue(value: string | null): void {
    const val = value ?? null;
    this.selectedValue.set(val);
    const text = this.resolveOption(val)?.text ?? '';
    this.inputValue.set(text);
    this.typedQuery.set(text);
  }

  registerOnChange(fn: (value: string | null) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled.set(isDisabled);
  }

  onInput(value: string): void {
    this.inputValue.set(value);
    this.typedQuery.set(value);
    this.isOpen.set(true);
    this.activeIndex.set(-1);

    if (this.suppressInline) {
      this.suppressInline = false;
      return;
    }

    const query = value.trim();
    if (!query) return;

    const match = this.inlineMatch();
    if (match) {
      const fullText = match.text;
      const input = this.inputEl()?.nativeElement;
      if (input) {
        input.value = fullText;
        this.inputValue.set(fullText);
        input.setSelectionRange(query.length, fullText.length);
      }
    }
  }

  onFocus(): void {
    if (this.isDisabled()) return;
    this.isOpen.set(true);
  }

  onBlur(): void {
    this.onTouched();
    this.isOpen.set(false);
    this.restoreSelectedDisplay();
  }

  onKeydown(event: KeyboardEvent): void {
    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        this.clearInlineSelection();
        if (!this.isOpen()) {
          this.isOpen.set(true);
        } else {
          const maxIdx = this.filteredOptions().length - 1;
          this.activeIndex.update((idx) => Math.min(idx + 1, maxIdx));
        }
        break;
      case 'ArrowUp':
        event.preventDefault();
        this.clearInlineSelection();
        this.activeIndex.update((idx) => Math.max(idx - 1, 0));
        break;
      case 'Enter':
        if (this.isOpen() && this.activeIndex() >= 0) {
          event.preventDefault();
          const option = this.filteredOptions()[this.activeIndex()];
          if (option) {
            this.selectOption(option);
          }
        } else {
          const input = this.inputEl()?.nativeElement;
          if (input && input.selectionStart !== input.selectionEnd) {
            event.preventDefault();
            input.selectionStart = input.selectionEnd;
            this.inputValue.set(input.value);
            const match = this.inlineMatch();
            if (match) {
              this.selectOption(match);
            }
          }
        }
        break;
      case 'Tab':
      case 'ArrowRight': {
        const input = this.inputEl()?.nativeElement;
        if (input && input.selectionStart !== input.selectionEnd) {
          if (event.key === 'ArrowRight') {
            event.preventDefault();
          }
          input.selectionStart = input.selectionEnd;
          this.inputValue.set(input.value);
          const match = this.inlineMatch();
          if (match) {
            this.selectOption(match);
          }
        }
        break;
      }
      case 'Backspace':
      case 'Delete':
        this.suppressInline = true;
        break;
      case 'Escape':
        this.isOpen.set(false);
        this.restoreSelectedDisplay();
        break;
    }
  }

  onOptionPointerdown(event: Event, option: GovukSelectOption<string | null>): void {
    event.preventDefault();
    this.selectOption(option);
  }

  @HostListener('document:pointerdown', ['$event'])
  onDocumentPointerdown(event: Event): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen.set(false);
      this.restoreSelectedDisplay();
    }
  }

  private clearInlineSelection(): void {
    const input = this.inputEl()?.nativeElement;
    if (input && input.selectionStart !== input.selectionEnd) {
      const typed = input.value.substring(0, input.selectionStart!);
      input.value = typed;
      this.inputValue.set(typed);
      this.typedQuery.set(typed);
    }
  }

  private selectOption(option: GovukSelectOption<string | null>): void {
    if (this.isDisabled() || option.disabled) return;

    const nextValue = option.value ?? null;
    this.selectedValue.set(nextValue);
    this.inputValue.set(option.text);
    this.typedQuery.set(option.text);
    this.activeIndex.set(-1);

    this.onChange(nextValue);
    this.onTouched();

    this.isOpen.set(false);
  }

  private restoreSelectedDisplay(): void {
    const selectedOption = this.resolveOption(this.selectedValue());
    const text = selectedOption?.text ?? '';
    this.inputValue.set(text);
    this.typedQuery.set(text);
  }

  private resolveOption(value: string | null): GovukSelectOption<string | null> | undefined {
    return this.options().find((option) => option.value === value);
  }
}
