import { HttpErrorResponse } from '@angular/common/http';

import { catchError, Observable, throwError, UnaryFunction } from 'rxjs';

export function catchElseRethrow<R, E = HttpErrorResponse>(
  predicate: UnaryFunction<E, boolean>,
  handler: UnaryFunction<E, Observable<R>>,
) {
  return <T>(source: Observable<T>): Observable<T | R> =>
    source.pipe(catchError((res: E) => (predicate(res) ? handler(res) : throwError(() => res))));
}

export function catchNotFoundRequest<R>(
  code: ErrorCode | ErrorCode[],
  handler: (res: HttpErrorResponse) => Observable<R>,
) {
  return catchElseRethrow((res) => isNotFoundRequest(res, code), handler);
}

export function isNotFoundRequest(res: unknown, codes?: ErrorCode | ErrorCode[]): res is NotFoundRequest {
  return (
    res instanceof HttpErrorResponse &&
    res.status === 404 &&
    (codes === undefined ||
      (typeof codes === 'string' && codes === res.error.code) ||
      (Array.isArray(codes) && codes.includes(res.error.code)))
  );
}

export enum ErrorCode {
  NOTFOUND1001 = 'NOTFOUND1001',
}

export interface NotFoundRequest extends HttpErrorResponse {
  status: 404;
  error: {
    code: ErrorCode;
    data: unknown;
  };
}
