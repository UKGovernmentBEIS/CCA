## Back link

Use the back link component to help users go back to the previous page in a multi-page transaction.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/back-link/).

In the app, prefer `netz-back-link` with the `backlink` route data property (see `projects/common/navigation/README.md`),
which renders this component for the active route.

### Inputs

- `link` (required): url relative to `route`, e.g. `'../'`.
- `route` (required): the `ActivatedRouteSnapshot` the link is resolved against.
- `fragment`: fragment appended to the link, e.g. the id of the tab to return to.
- `queryParams`: query params of the link. Defaults to the route's query params, or none when `fragment` is set.
- `inverse`: use `true` for back links on dark backgrounds.
  (https://design-system.service.gov.uk/components/back-link/#back-links-on-dark-backgrounds)

### Example

```html
<govuk-back-link [link]="'../'" [route]="route" />
```

```html
<govuk-back-link [link]="'../../'" [fragment]="'reports'" [route]="route" [inverse]="true" />
```
