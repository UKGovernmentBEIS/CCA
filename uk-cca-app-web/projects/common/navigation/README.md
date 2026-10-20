# NAVIGATION

## Specs

- Use the breadcrumb to depict where users are within a website's structure and allow them to move between levels.

- The backlink component should never be used together with breadcrumbs according to the GDS guidelines. Source: [Back link](https://design-system.service.gov.uk/components/back-link/)

- The breadcrumbs should end with the parent section of the current page. Source: [Breadcrumbs](https://design-system.service.gov.uk/components/breadcrumbs/) 

## Breadcrumbs

Breadcrumbs are the UI element which helps the user track their journey, usually placed close to the top of a page. Technically, we've chosen to tie breadcrumbs with routing. This mostly works.
Below we give ways to override this behavior, in case that's needed.


**_Example:_**

```typescript
export const ROUTES: Routes = [
  {
    path: '',
    component: HomeComponent,
    data: { breadcrumb: 'Home' },
    children: [
      {
        path: 'heroes',
        component: HeroListComponent,
        data: { breadcrumb: 'Heroes' },
      },
      {
        path: 'about',
        component: AboutComponent,
        data: { breadcrumb: 'About' },
      },
    ],
  },
];

Assuming the user is on the heroes route the expected output is:

Home > Heroes.

The url of the link is determined by the route tree. This can also be ovewritten.

```

The `breadcrumb` property found in each route can be one of the following:

- `string`: This is for when the route's breadcrumb is a static string e.g. In this case, the breadcrumb link is generated from code.
- `object`: This is for when you need additional properties. The object has a `text` property, plus optional `link`, `from` and `fragment`.
  - `link`: explicit link target. Takes precedence over `from`.
  - `from`: the route config `path` of an ancestor (e.g. `:facilityId`). Use it instead of `link` when the crumb
    should point at a parent route: the link resolves to that ancestor's URL, so the same route can be mounted under
    different URL trees (e.g. `/target-unit-accounts/…` and `/sectors/…/target-units/…`) and still link to its parent.
  - `fragment`: fragment appended to the resolved link.
- `function`: This is for when the breacrumb properties are dynamic. The function takes as input the route resolve data and can return either a string or an object with `text`, `link` or `from`, and possibly `fragment`. Returning `false` or `null` hides the breadcrumb.
- `false`: You can hide a breadcrumb by passing false as a value.

### Examples

```typescript
export const ROUTES: Routes = [
  {
    path: '',
    component: HomeComponent,
    data: { breadcrumb: 'Home' },
    children: [
      {
        path: 'heroes',
        component: HeroListComponent,
        data: { breadcrumb: 'Heroes' },
        children: [
          {
            path:':id',
            resolve: { name: resolveHeroNameFn }
            data: { breadcrumb: ({ name }) => `Super hero named ${name}`} // dynamically adding the breadcrumb text
            children: [
              {
                path: '',
                component: HeroComponent,
              },
              {
                path: 'reports/:reportId',
                component: ReportDetailsComponent,
                data: {
                  // links to the nearest `:id` ancestor route, whatever tree this route was entered through
                  breadcrumb: { text: 'Report details', from: ':id', fragment: 'reports' }
                },
              },
            ]
          },
        ]
      },
      {
        path: 'about',
        component: AboutComponent,
        data: { breadcrumb: false }, // hides the breadcrumb
      },
      {
        path: 'settings',
        component: SettingsComponent,
        data: { 
          breadcrumb: { text: 'Settings', fragment: 'user-profile', link:'/settings' } // breadcrumb with fragment
        },
      },
    ],
  },
];
```
You can still completely overwrite the breadcrumb at the component level.
While fragments can now be set directly in the route data, this approach is useful when you need more control over the breadcrumb links or when you need to include query parameters.

breacrumb element type
```typescript

type Link = {
  text: string;
  link: string | string[];
  queryParams?: Map<string, string | number>;
  fragment?: string;
};

```

```typescript
    this.breadcrumbService.show([
      {
        text: 'Dashboard',
        link: ['/', 'dashboard'],
      },
      {
        text: 'Subsistence fees',
        link: ['/', 'subsistence-fees'],
        fragment: 'sent-subsistence-fees',
      },
    ]);
```

---

## Back links

Back links can only be defined on a route's data property.  
They can be one of:

- `string`: In this case you must provide a url relative to the current route e.g

```typescript
const routes: Routes = [
  {
    path: '',
    component: ManagementProceduresRolesComponent,
  },
  {
    path: 'documentation',
    data: { backlink: '../' },
    component: ManagementProceduresDocumentationComponent,
  },
  {
    path: 'responsibilities',
    data: { backlink: '../documentation' },
    component: ManagementProceduresResponsibilitiesComponent,
  },
];
```

- `function`: The function must be of the form `(data: Data) => string` where `data` is the route's data. This is
  commonly used together with a [ResolveFn](https://angular.io/api/router/ResolveFn) that can populate the route's
  data with relevant information. You can then extract the info you need from the `Data` object to get the
  back link url e.g.

```typescript
const resolveEnvironmentalManagementBackLink: ResolveFn<any> = () => {
  return inject(RequestTaskStore).pipe(
    monitoringApproachQuery.selectMonitoringApproach,
    map((approach) => {
      return approach.monitoringApproachType === 'FUEL_USE_MONITORING' ? '../uplift-quantity' : '../risks';
    }),
  );
};

const route = {
  path: 'environmental-management',
  data: { backlink: ({ backlinkUrl }) => backlinkUrl },
  resolve: { backlinkUrl: resolveEnvironmentalManagementBackLink },
  component: EnvironmentalManagementComponent,
};
```
