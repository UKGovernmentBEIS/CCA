The header and service navigation components render the GOV.UK v6 header and the
navigation bar below it. Together with `<govuk-skip-link>` they form the top of the page.

1. `<govuk-header>` renders the blue GOV.UK brand bar with the crown logo. The logo
   links to `//gov.uk` by default; override it with the `homepage` input. Optional
   `productName` shows a product name next to the logo. Projected content is rendered
   in the actions area on the right-hand side of the bar (e.g. "Sign out").
2. `<govuk-service-navigation>` renders the service name and its top-level navigation.
   On small screens the links collapse behind a Menu button, mirroring the GDS
   component's behaviour.
3. `<govuk-skip-link>` renders the skip link for keyboard users.

The service name and navigation are inputs of `<govuk-service-navigation>`, not of
`<govuk-header>` — GOV.UK Frontend v6 removed the service name and navigation from
the header.

Wrap the header and service navigation in `<header class="govuk-template__header">`.
The components render `<div>` elements, matching the GOV.UK Frontend v6 Nunjucks
macros; the wrapper supplies the banner landmark from the GOV.UK page template.

```html
<header class="govuk-template__header">
  <govuk-header>
    <a class="govuk-link govuk-link--inverse" routerLink (click)="logout($event)">Sign out</a>
  </govuk-header>

  <govuk-service-navigation
    serviceName="Manage your Climate Change Agreements"
    [serviceHomepage]="{ routerLink: '/' }"
    [navigation]="[
      { text: 'Tasks', routerLink: '/dashboard' },
      { text: 'Target unit accounts', routerLink: '/target-unit-accounts' },
    ]" />
</header>
```
