import { isDevMode } from '@angular/core';

/**
 * Production-safe logger that suppresses console output in production builds.
 * `isDevMode()` is false whenever the CLI builds with `optimization`, which the production configuration does.
 *
 * Use this instead of direct console.* calls for any logging that may contain
 * sensitive information (API paths, error details, stack traces).
 */
export const logger = {
  error(...args: unknown[]): void {
    if (isDevMode()) console.error(...args);
  },
  warn(...args: unknown[]): void {
    if (isDevMode()) console.warn(...args);
  },
  log(...args: unknown[]): void {
    if (isDevMode()) console.log(...args);
  },
};
