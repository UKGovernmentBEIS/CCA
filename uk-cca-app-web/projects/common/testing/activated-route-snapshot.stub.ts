import { ActivatedRouteSnapshot, convertToParamMap, Data, ParamMap, Params, Route, UrlSegment } from '@angular/router';

/**
 * An ActivateRoute test double with a `paramMap` observable.
 * Use the `setParamMap()` method to add the next `paramMap` value.
 */
export class ActivatedRouteSnapshotStub implements ActivatedRouteSnapshot {
  // Use a ReplaySubject to share previous values with subscribers
  // and pump new values into the `paramMap` observable
  paramMap!: ParamMap;
  readonly queryParamMap!: ParamMap;
  readonly data!: Data;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- implements Angular's ActivatedRouteSnapshot.component: Type<any> | string | null
  component: any | string | null = null;
  fragment!: string;
  outlet!: string;
  params!: Params;
  queryParams!: Params;
  readonly routeConfig: Route | null = null;
  title = '';

  constructor(initialParams?: Params, initialQueryParams?: Params, resolves?: Data) {
    this.paramMap = convertToParamMap(initialParams ?? {});
    this.queryParamMap = convertToParamMap(initialQueryParams ?? {});
    this.data = resolves ?? {};
    this.fragment = '';
    this.outlet = '';
    this.params = initialParams ?? {};
    this.queryParams = initialQueryParams ?? {};
  }

  get children(): ActivatedRouteSnapshot[] {
    return [];
  }

  readonly firstChild: ActivatedRouteSnapshot | null = null;

  readonly parent: ActivatedRouteSnapshot | null = null;

  get pathFromRoot(): ActivatedRouteSnapshot[] {
    return [];
  }

  get root(): ActivatedRouteSnapshot {
    return this as unknown as ActivatedRouteSnapshot;
  }

  get url(): UrlSegment[] {
    return [];
  }
}
