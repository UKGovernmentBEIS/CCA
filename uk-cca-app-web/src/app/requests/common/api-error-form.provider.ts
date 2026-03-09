import { HttpErrorResponse } from '@angular/common/http';
import { InjectionToken, Provider } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

export type ApiErrorFormModel = FormGroup<{
  error: FormControl<string>;
}>;

export const API_ERROR_FORM = new InjectionToken<ApiErrorFormModel>('API error form');

export const ApiErrorFormProvider: Provider = {
  provide: API_ERROR_FORM,
  useFactory: () => {
    return new FormGroup({
      error: new FormControl<string>(null),
    });
  },
};

export function setApiErrors(form: ApiErrorFormModel, errorResponse: HttpErrorResponse): void {
  const data = errorResponse?.error?.data;

  if (Array.isArray(data) && data.length > 0) {
    const errors: Record<string, string> = {};
    data.forEach((item: { message: string; data?: unknown[] }, index: number) => {
      const itemData = item.data?.flat(Infinity)?.join(', ');
      errors[`apiError_${index}`] = itemData ? `${item.message} (${itemData})` : item.message;
    });
    form.controls.error.setErrors(errors);
  } else {
    const message = errorResponse?.error?.message ?? 'An unexpected error occurred';
    form.controls.error.setErrors({ apiError: message });
  }
}
