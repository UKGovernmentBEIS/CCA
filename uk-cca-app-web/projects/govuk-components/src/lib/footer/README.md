The footer component renders the GOV.UK footer, including the crown logo, the Open
Government Licence and the Crown copyright link.

The `navigation` input takes columns of links shown above the meta row. The `meta`
input takes the support links (cookies, accessibility, contact and similar), shown in
the meta row. Both accept items with either a `routerLink` (navigation inside the
app) or an `href` (links leaving the app). Projected content is rendered below the
support links.

Wrap the footer in `<footer class="govuk-template__footer">`. The component renders a
`<div>`, matching the GOV.UK Frontend v6 Nunjucks macro; the wrapper supplies the
contentinfo landmark from the GOV.UK page template.

```html
<footer class="govuk-template__footer">
  <govuk-footer
    [meta]="[
      { text: 'Privacy Notice', routerLink: '/privacy-notice' },
      { text: 'Contact us', routerLink: '/contact-us' },
      { text: 'Accessibility Statement', routerLink: '/accessibility' },
    ]" />
</footer>
```
