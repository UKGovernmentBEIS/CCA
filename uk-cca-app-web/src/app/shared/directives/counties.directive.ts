import { ChangeDetectorRef, Directive, OnInit } from '@angular/core';

import { map } from 'rxjs';

import { SelectComponent } from '@netz/govuk-components';
import { CountyService } from '@shared/services';
import { County } from '@shared/types';

@Directive({
  selector: 'govuk-select[ccaCounties],[govuk-select][ccaCounties]',
  standalone: true,
})
export class CountiesDirective implements OnInit {
  constructor(
    private readonly apiService: CountyService,
    private readonly selectComponent: SelectComponent,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.apiService
      .getUkCounties()
      .pipe(
        map((counties: County[]) =>
          counties
            .sort((a: County, b: County) => (a.name > b.name ? 1 : -1))
            .map((county) => ({
              text: county.name,
              value: county.name,
            })),
        ),
      )
      .subscribe((res) => {
        this.selectComponent.options = res;
        this.changeDetectorRef.markForCheck();
      });
  }
}
