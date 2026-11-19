import { TestBed } from '@angular/core/testing';

import { PendingRequestService } from '@netz/common/services';

import { isStaleChunkError, StaleChunkRecoveryService } from './stale-chunk-recovery.service';

const CHROMIUM_ERROR = new TypeError('Failed to fetch dynamically imported module: http://localhost/chunk-ABCD1234.js');
const FIREFOX_ERROR = new TypeError('error loading dynamically imported module: http://localhost/chunk-ABCD1234.js');
const SAFARI_ERROR = new TypeError('Importing a module script failed.');

describe('isStaleChunkError', () => {
  it.each([
    ['Chromium', CHROMIUM_ERROR],
    ['Firefox', FIREFOX_ERROR],
    ['Safari', SAFARI_ERROR],
  ])('should recognise the %s dynamic import failure', (_, error) => {
    expect(isStaleChunkError(error)).toBe(true);
  });

  it.each([
    ['an unrelated error', new Error('Something else failed')],
    ['a matching message on an error that is not a TypeError', new Error(CHROMIUM_ERROR.message)],
    ['a matching message passed as a string', CHROMIUM_ERROR.message],
    ['null', null],
  ])('should not recognise %s', (_, error) => {
    expect(isStaleChunkError(error)).toBe(false);
  });
});

describe('StaleChunkRecoveryService', () => {
  let service: StaleChunkRecoveryService;
  let reloadSpy: ReturnType<typeof vi.fn<() => void>>;
  let loadPageSpy: ReturnType<typeof vi.fn<(url: string) => void>>;

  beforeEach(() => {
    sessionStorage.clear();
    vi.spyOn(console, 'warn').mockImplementation(() => undefined);

    service = TestBed.inject(StaleChunkRecoveryService);
    reloadSpy = vi.fn<() => void>();
    loadPageSpy = vi.fn<(url: string) => void>();
    vi.spyOn(service as unknown as { reloadPage: () => void }, 'reloadPage').mockImplementation(reloadSpy);
    vi.spyOn(service as unknown as { loadPage: (url: string) => void }, 'loadPage').mockImplementation(loadPageSpy);
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.restoreAllMocks();
  });

  it('should ignore errors that are not stale chunk failures', () => {
    expect(service.recover(new Error('Something else failed'))).toBe(false);
    expect(reloadSpy).not.toHaveBeenCalled();
  });

  it('should reload the page once for a stale chunk failure', () => {
    expect(service.recover(CHROMIUM_ERROR)).toBe(true);
    expect(service.recover(CHROMIUM_ERROR)).toBe(true);

    expect(reloadSpy).toHaveBeenCalledTimes(1);
  });

  it('should not reload again when the chunk fails right after a reload', () => {
    sessionStorage.setItem(StaleChunkRecoveryService.RELOADED_AT_KEY, String(Date.now()));

    expect(service.recover(CHROMIUM_ERROR)).toBe(false);
    expect(reloadSpy).not.toHaveBeenCalled();
  });

  it('should reload again once the reload loop window has passed', () => {
    vi.useFakeTimers();
    sessionStorage.setItem(StaleChunkRecoveryService.RELOADED_AT_KEY, String(Date.now()));
    vi.advanceTimersByTime(StaleChunkRecoveryService.RELOAD_LOOP_WINDOW_MS);

    expect(service.recover(CHROMIUM_ERROR)).toBe(true);
    expect(reloadSpy).toHaveBeenCalledTimes(1);
  });

  it('should load the navigation target instead of reloading the current page', () => {
    expect(service.recover(CHROMIUM_ERROR, '/tasks/1')).toBe(true);

    expect(loadPageSpy).toHaveBeenCalledWith('/tasks/1');
    expect(reloadSpy).not.toHaveBeenCalled();
  });

  it('should not reload while the browser is offline', () => {
    vi.spyOn(navigator, 'onLine', 'get').mockReturnValue(false);

    expect(service.recover(CHROMIUM_ERROR)).toBe(false);
    expect(reloadSpy).not.toHaveBeenCalled();
  });

  it('should not reload while a request is pending', () => {
    vi.spyOn(TestBed.inject(PendingRequestService), 'hasPendingRequests').mockReturnValue(true);

    expect(service.recover(CHROMIUM_ERROR)).toBe(false);
    expect(reloadSpy).not.toHaveBeenCalled();
  });

  it('should not reload when session storage is unavailable', () => {
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new DOMException('The operation is insecure.', 'SecurityError');
    });

    expect(service.recover(CHROMIUM_ERROR)).toBe(false);
    expect(reloadSpy).not.toHaveBeenCalled();
  });
});
