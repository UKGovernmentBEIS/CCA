import { DOCUMENT, Location } from '@angular/common';
import { inject, Service } from '@angular/core';
import { NavigationError, Router } from '@angular/router';

import { PendingRequestService } from '@netz/common/services';

import { logger } from '../utils/logger';

/**
 * Reloads the page when a lazy chunk fails to load during a rolling deployment.
 *
 * Every chunk filename carries a content hash. A tab opened before a deployment requests chunk hashes that the
 * updated pods no longer serve. A full page load fetches a fresh `index.html` that references the new chunks.
 */

// Dynamic import failure messages from Chromium, Firefox and Safari. All of them are raised as a `TypeError`.
const STALE_CHUNK_ERROR_MESSAGE =
  /Failed to fetch dynamically imported module|error loading dynamically imported module|Importing a module script failed/i;

export function isStaleChunkError(error: unknown): boolean {
  return error instanceof TypeError && STALE_CHUNK_ERROR_MESSAGE.test(error.message);
}

/**
 * Router navigation error handler: loads the navigation target when a lazy route's chunk is stale. The browser URL
 * may not show the target yet, so a plain reload could land on the previous page. A navigation that skips the
 * location change reloads the current page instead, so its URL is not exposed.
 */
export function recoverFromStaleChunkNavigation(event: NavigationError): void {
  const skipLocationChange = inject(Router).currentNavigation()?.extras?.skipLocationChange;
  inject(StaleChunkRecoveryService).recover(event.error, skipLocationChange ? undefined : event.url);
}

@Service()
export class StaleChunkRecoveryService {
  static readonly RELOADED_AT_KEY = 'cca_stale_chunk_reloaded_at';

  // A chunk that still fails this soon after a reload is missing from the deployment. The error page is shown
  // instead, so the user is not trapped in a reload loop.
  static readonly RELOAD_LOOP_WINDOW_MS = 10 * 1000;

  private readonly document = inject(DOCUMENT);
  private readonly location = inject(Location);
  private readonly pendingRequestService = inject(PendingRequestService);
  private reloading = false;

  /**
   * Loads a fresh copy of the page when `error` is a stale chunk failure: `url` when given, the current page
   * otherwise. Returns `true` when the page load is under way, so callers must not show an error page over it.
   */
  recover(error: unknown, url?: string): boolean {
    if (!isStaleChunkError(error)) return false;

    // The router reports a failed navigation to both the navigation error handler and the ErrorHandler.
    if (this.reloading) return true;

    // An offline browser fails the import the same way, and a page load would not succeed either.
    if (!this.document.defaultView.navigator.onLine) {
      logger.warn('[StaleChunkRecovery] Chunk failed to load while offline, not reloading', error);
      return false;
    }

    // A page load would abort a pending save and lose the user's progress.
    if (this.pendingRequestService.hasPendingRequests()) {
      logger.warn('[StaleChunkRecovery] Chunk failed to load while a request is pending, not reloading', error);
      return false;
    }

    if (!this.markReload()) {
      logger.warn('[StaleChunkRecovery] Chunk failed to load again right after a reload, not reloading', error);
      return false;
    }

    logger.warn('[StaleChunkRecovery] Chunk failed to load, reloading the page', error);
    this.reloading = true;
    url ? this.loadPage(this.location.prepareExternalUrl(url)) : this.reloadPage();

    return true;
  }

  /**
   * Records the reload time. Returns `false` when the previous reload was too recent, or when session storage is
   * unavailable and a reload loop could not be detected.
   */
  private markReload(): boolean {
    try {
      const storage = this.document.defaultView.sessionStorage;
      const reloadedAt = Number(storage.getItem(StaleChunkRecoveryService.RELOADED_AT_KEY));

      if (Date.now() - reloadedAt < StaleChunkRecoveryService.RELOAD_LOOP_WINDOW_MS) return false;

      storage.setItem(StaleChunkRecoveryService.RELOADED_AT_KEY, String(Date.now()));
      return true;
    } catch {
      return false;
    }
  }

  private loadPage(url: string): void {
    this.document.location.assign(url);
  }

  private reloadPage(): void {
    this.document.location.reload();
  }
}
