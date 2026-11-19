import { AsyncPipe, KeyValue, KeyValuePipe, NgTemplateOutlet } from '@angular/common';
import {
  afterNextRender,
  AfterViewInit,
  Component,
  DOCUMENT,
  ElementRef,
  inject,
  Injector,
  input,
  OnChanges,
  viewChild,
  viewChildren,
} from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormControlStatus,
  FormSubmittedEvent,
  NgForm,
  UntypedFormArray,
  UntypedFormGroup,
} from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';

import { filter, map, Observable, startWith, switchMap, take, tap } from 'rxjs';

import { FormService } from '../form';
import { displayValidationErrors } from '../error-message/display-validation-errors';
import { NestedMessageValidationErrors } from './nested-message-validation-errors';

@Component({
  selector: 'govuk-error-summary',
  templateUrl: './error-summary.component.html',
  imports: [RouterLink, KeyValuePipe, AsyncPipe, NgTemplateOutlet],
})
export class ErrorSummaryComponent implements OnChanges, AfterViewInit {
  private readonly document = inject(DOCUMENT);
  private readonly formService = inject(FormService);
  private readonly title = inject(Title);
  private readonly injector = inject(Injector);

  readonly form = input<UntypedFormGroup | NgForm>(null);

  readonly inputs = viewChildren('anchor', { read: ElementRef });
  readonly container = viewChild('container', { read: ElementRef });

  errorList$: Observable<NestedMessageValidationErrors>;

  private formControl: UntypedFormGroup;

  constructor() {
    // A summary that mounts on submit is focused by ngAfterViewInit. One that is already mounted
    // only re-renders its list, so move focus on every later submit that leaves errors, once any
    // async validation has settled.
    toObservable(this.form)
      .pipe(
        map((form) => (form instanceof UntypedFormGroup ? form : form?.control)),
        filter(Boolean),
        switchMap((control) =>
          control.events.pipe(
            filter((event) => event instanceof FormSubmittedEvent),
            switchMap(() =>
              control.statusChanges.pipe(
                startWith(control.status),
                filter((status) => status !== 'PENDING'),
                take(1),
                filter((status) => status === 'INVALID'),
              ),
            ),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe(() => afterNextRender({ read: () => this.focusContainer() }, { injector: this.injector }));
  }

  ngOnChanges(): void {
    const form = this.form();
    this.formControl = form instanceof UntypedFormGroup ? form : form.control;

    const statusChanges: Observable<FormControlStatus> = this.form().statusChanges;
    this.errorList$ = statusChanges.pipe(
      startWith(this.form().status),
      map((status) => status === 'INVALID' && this.getAbstractControlErrors(this.formControl)),
      tap((errors) => {
        const currentTitle = this.title.getTitle();
        const prefix = 'Error: ';

        if (errors && !currentTitle.startsWith(prefix)) {
          this.title.setTitle(prefix.concat(currentTitle));
        } else if (!errors) {
          this.title.setTitle(currentTitle.replace(prefix, ''));
        }
      }),
    );
  }

  ngAfterViewInit(): void {
    this.focusContainer();
  }

  /**
   * Moves focus to the summary after the next render. The summary refocuses by itself when it mounts and after a
   * form submit; pages that set errors from an API response or a button click call this from their error branch.
   */
  focus(): void {
    afterNextRender({ read: () => this.focusContainer() }, { injector: this.injector });
  }

  errorClick(path: string): void {
    if (!path) return;

    const labelOrLegend = this.document.getElementById(`l.${path}`);
    if (labelOrLegend) {
      labelOrLegend.scrollIntoView();
    }

    // Case radio - first option
    let targetInput: HTMLElement = this.document.getElementById(`${path}-option0`);
    if (!targetInput) {
      // Case checkbox - first checkbox
      targetInput = this.document.getElementById(`${path}-0`);
    }
    if (!targetInput) {
      // Case date - first input with error
      targetInput = this.document.getElementById(`${path}-day`);
      if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
        targetInput = this.document.getElementById(`${path}-month`);
        if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
          targetInput = this.document.getElementById(`${path}-year`);
          if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
            targetInput = this.document.getElementById(`${path}-day`);
          }
        }
      }
    }

    // Case text, textarea, select
    if (!targetInput) targetInput = this.document.getElementById(path);
    if (targetInput) targetInput.focus({ preventScroll: true });
  }

  sortByPosition = (
    a: KeyValue<string, NestedMessageValidationErrors>,
    b: KeyValue<string, NestedMessageValidationErrors>,
  ) => {
    const combinedSelector = [a, b]
      .map(({ value }) => this.findFirstLeafSelector(value) ?? '')
      .filter((selector) => !!selector)
      .map((selector) => `#${this.sanitizeSelector(selector)}`)
      .join(', ');
    const elements: HTMLElement[] = Array.from(this.document.querySelectorAll(combinedSelector));
    const aIndex = elements.findIndex((element) => element.id === a.key);
    const bIndex = elements.findIndex((element) => element.id === b.key);

    return aIndex === -1 ? 1 : bIndex === -1 ? -1 : aIndex - bIndex;
  };

  private getAbstractControlErrors(control: AbstractControl, path: string[] = []): NestedMessageValidationErrors {
    let childControlErrors: Record<string, NestedMessageValidationErrors>;

    if (control instanceof UntypedFormGroup) {
      childControlErrors = Object.entries(control.controls)
        .map(([key, value]) => ({ [key]: this.getAbstractControlErrors(value, path.concat([key])) }))
        .reduce((errors, controlErrors) => ({ ...errors, ...controlErrors }), {});
    } else if (control instanceof UntypedFormArray) {
      childControlErrors = control.controls
        .map((arrayControlItem, index) => ({
          [index]: this.getAbstractControlErrors(arrayControlItem, path.concat([String(index)])),
        }))
        .reduce((errors, controlErrors) => ({ ...errors, ...controlErrors }), {});
    }

    return {
      path: this.formService.getIdentifier(path),
      self: displayValidationErrors(control.errors),
      controls: childControlErrors,
    };
  }

  private focusContainer(): void {
    const container = this.container();
    if (container?.nativeElement?.scrollIntoView) container.nativeElement.scrollIntoView();
    if (container?.nativeElement?.focus) container.nativeElement.focus();
  }

  private sanitizeSelector(selector: string): string {
    return selector.replace(/\./g, '\\.');
  }

  private findFirstLeafSelector(error: NestedMessageValidationErrors): string {
    return error.controls
      ? Object.values(error.controls)
          .map((control) => control.path || this.findFirstLeafSelector(control))
          .find((path) => path)
      : error.path;
  }
}
