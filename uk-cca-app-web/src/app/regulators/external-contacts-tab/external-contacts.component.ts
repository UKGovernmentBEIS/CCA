import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { ButtonDirective, GovukTableColumn, SortEvent, TableComponent } from '@netz/govuk-components';

import { CaExternalContactDTO, CaExternalContactsService } from 'cca-api';

@Component({
  selector: 'cca-external-contacts',
  templateUrl: './external-contacts.component.html',
  standalone: true,
  imports: [RouterLink, ButtonDirective, TableComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExternalContactsComponent {
  private readonly externalContactsService = inject(CaExternalContactsService);

  protected readonly contactsResponse = toSignal(this.externalContactsService.getCaExternalContacts());
  protected readonly sorting = signal<SortEvent>({ column: 'lastUpdatedDate', direction: 'ascending' });

  protected readonly contacts = computed(() => {
    const contacts = this.contactsResponse()?.caExternalContacts || [];
    return contacts.slice().sort(this.sortContacts(this.sorting()));
  });

  protected readonly isEditable = computed(() => this.contactsResponse()?.isEditable);

  protected readonly editableColumns: GovukTableColumn<CaExternalContactDTO>[] = [
    { field: 'name', header: 'Displayed name', isSortable: true, isHeader: true },
    { field: 'email', header: 'Email address', isSortable: true },
    { field: 'description', header: 'Description' },
    { field: null, header: null },
  ];

  protected readonly nonEditableColumns = this.editableColumns.slice(0, 3);

  private sortContacts(sorting: SortEvent): (a: CaExternalContactDTO, b: CaExternalContactDTO) => number {
    return (a, b) => {
      let diff: number;

      switch (sorting.column) {
        case 'name':
        case 'email':
          diff = a[sorting.column].localeCompare(b[sorting.column], 'en-GB', { numeric: true, sensitivity: 'base' });
          break;
        default:
          diff = new Date(a.lastUpdatedDate).valueOf() - new Date(b.lastUpdatedDate).valueOf();
          break;
      }

      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    };
  }
}
