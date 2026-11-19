import { AsyncPipe } from '@angular/common';
import {
  afterNextRender,
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  Injector,
  input,
  OnChanges,
  viewChild,
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

import { filter, map, Observable, startWith, switchMap, take, tap } from 'rxjs';

import { DetailsComponent, FormService } from '@netz/govuk-components';

import { NestedMessageValidationError } from './nested-message-validation-error.interface';

@Component({
  selector: 'cca-csv-error-summary',
  templateUrl: './csv-error-summary.component.html',
  imports: [AsyncPipe, DetailsComponent],
})
export class CsvErrorSummaryComponent implements OnChanges, AfterViewInit {
  private readonly formService = inject(FormService);
  private readonly title = inject(Title);
  private readonly injector = inject(Injector);

  protected readonly form = input<UntypedFormGroup | NgForm>(undefined);

  protected readonly container = viewChild('container', { read: ElementRef });

  private formControl: UntypedFormGroup;

  errorList$: Observable<NestedMessageValidationError[]>;

  constructor() {
    // A summary that mounts on submit is focused by ngAfterViewInit. One that is already mounted
    // only re-renders its list, so move focus on every later submit that leaves errors, once the
    // async file validation has settled.
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
      map((status) => (status === 'INVALID' ? this.getAbstractControlErrors(this.formControl) : null)),
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

  private getAbstractControlErrors(control: AbstractControl, path: string[] = []): NestedMessageValidationError[] {
    let childControlErrors: NestedMessageValidationError[] = [];

    if (control instanceof UntypedFormGroup) {
      childControlErrors = Object.entries(control.controls)
        .map(([key, value]) => this.getAbstractControlErrors(value, path.concat([key])))
        .reduce((errors, controlErrors) => errors.concat(controlErrors), []);
    } else if (control instanceof UntypedFormArray) {
      childControlErrors = control.controls
        .map((arrayControlItem, index) => this.getAbstractControlErrors(arrayControlItem, path.concat([String(index)])))
        .reduce((errors, controlErrors) => errors.concat(controlErrors), []);
    }

    const errors = control.errors;

    if (errors) {
      const errorEntries = Object.keys(errors).map((key) => ({
        type: key,
        path: this.formService.getIdentifier(path),
        ...errors[key],
      }));

      return childControlErrors.concat(errorEntries);
    }

    return childControlErrors;
  }

  private focusContainer(): void {
    const container = this.container();
    if (container?.nativeElement?.scrollIntoView) container.nativeElement.scrollIntoView();
    if (container?.nativeElement?.focus) container.nativeElement.focus();
  }

  getRowIndexes(rows: Array<{ rowIndex: number }>): number[] {
    return rows.map((row) => row.rowIndex);
  }
}
