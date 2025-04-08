import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'markingOfFacilitiesStatus',
  standalone: true,
})
export class MarkingOfFacilitiesStatusPipe implements PipeTransform {
  transform(totalFacilities: number, paidFacilities: number): string {
    if (totalFacilities == null || paidFacilities == null) {
      throw new Error(`invalid marking of facilities amounts. Received: ${totalFacilities} and ${paidFacilities}`);
    }

    if (totalFacilities === 0) return 'Cancelled';
    if (totalFacilities > paidFacilities) return 'In progress';
    if (totalFacilities <= paidFacilities) return 'Completed';
  }
}
