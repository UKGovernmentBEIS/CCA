import { ActivatedRouteSnapshot, Data, Params } from '@angular/router';

import { describe, expect, it, vi } from 'vitest';

import { resolveBacklink } from './resolve-backlink';

const snapshot = (data: Data, queryParams: Params = {}): ActivatedRouteSnapshot =>
  ({ data, queryParams }) as unknown as ActivatedRouteSnapshot;

describe('resolveBacklink', () => {
  it('returns null when the route has no back link', () => {
    expect(resolveBacklink(snapshot({}))).toBeNull();
  });

  it('returns null when the back link is false', () => {
    expect(resolveBacklink(snapshot({ backlink: false }))).toBeNull();
  });

  it('returns null when the back link is an empty string', () => {
    expect(resolveBacklink(snapshot({ backlink: '' }))).toBeNull();
  });

  it('resolves a string to a link', () => {
    expect(resolveBacklink(snapshot({ backlink: '../' }))).toEqual({ link: '../' });
  });

  it('resolves an object as is', () => {
    expect(resolveBacklink(snapshot({ backlink: { link: '../', fragment: 'reports' } }))).toEqual({
      link: '../',
      fragment: 'reports',
    });
  });

  it('calls a function with the route data and snapshot', () => {
    const backlink = vi.fn(() => '../');
    const route = snapshot({ backlink, sectorId: 5 });

    expect(resolveBacklink(route)).toEqual({ link: '../' });
    expect(backlink).toHaveBeenCalledWith(route.data, route);
  });

  it('resolves a function returning an object', () => {
    const backlink = (_data: Data, route: ActivatedRouteSnapshot) =>
      route.queryParams.change ? { link: '../..', fragment: 'audit' } : '..';

    expect(resolveBacklink(snapshot({ backlink }, { change: 'true' }))).toEqual({ link: '../..', fragment: 'audit' });
    expect(resolveBacklink(snapshot({ backlink }))).toEqual({ link: '..' });
  });

  it('returns null when a function returns false or null', () => {
    expect(resolveBacklink(snapshot({ backlink: (): false => false }))).toBeNull();
    expect(resolveBacklink(snapshot({ backlink: (): null => null }))).toBeNull();
  });
});
