## Table component

The `table` component renders a table following the GOV.UK design standards.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/table/).

If an `<ng-template>` is provided as component content, then it is used as a cell template.
Its context consists of the current `row` and `column` variables.

***Important*** - The data passed to the component must contain an `isSelectable` property for the table to work properly and  when to show the checkbox or not if you don't include this property, the table considers the element as non-selectable.

### Inputs

- `data` - An array of objects of any type.
- `columns` - An array of objects following the `CcaTableColumn` interface. The `field` property is used to determine which value of each object will be rendered.
  The `primary` property is used to determine if the column will be considered as the primary field to be used for handling unique data.
- `caption` - A string to be used as a caption.
- `selectedRows` - A Map containing any selected rows.

### Outputs

- `sort` - An event emitted whenever a sortable column is clicked.
  The event object contains a column property, with the respective column field,
  as well as a direction property with the sorting direction.

- `rowSelectionChange` - An event emitted whenever any row is selected, containing all row data as an array.
- `allRowsSelectionChange` - An event emitted whenever all rows are selected from the checkbox in the header.

### Example

```html
<cca-table [columns]="columns" [data]="data" [selectedRows]="selectedRows" (rowSelectionChange)="onSelectChange($event)" (allRowsSelectionChange)="onSelectWholePageChange($event)">
  <ng-template let-column="column" let-row="row">
    <a [routerLink]="row.url">{{row[column.field]}}</a>
  </ng-template>
</cca-table>
```

```typescript
class MyComponent {
  columns: GovukTableColumn[] = [
    { header: 'Name', field: 'name', widthClass: 'govuk-!-width-one-quarter', primary: true },
    { header: 'Surname', field: 'surname' },
    { header: 'Age', field: 'age', isSortable: true },
  ];

  data: any[] = [
    { name: 'Name 1', surname: 'Surname 1', age: 23, isSelectable: true },
    { name: 'Name 2', surname: 'Surname 2', age: 48, isSelectable: false },
    { name: 'Name 3', surname: 'Surname 3', age: 32, isSelectable: true },
  ];

  onSelectChange(selection: SelectionDataModel[]) {
    // do whatever you need with these data
  }
  
  onSelectWholePageChange(areAllSelected: boolean) {
    // do whatever you need with these data
  }
}
```
