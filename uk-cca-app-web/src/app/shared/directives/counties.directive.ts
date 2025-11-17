import { ChangeDetectorRef, Directive, inject, OnInit } from '@angular/core';

import { map, of } from 'rxjs';

import { SelectComponent } from '@netz/govuk-components';
import { COUNTIES } from '@shared/services';
import { County } from '@shared/types';

@Directive({
  selector: 'govuk-select[ccaCounties],[govuk-select][ccaCounties]',
})
export class CountiesDirective implements OnInit {
  private readonly selectComponent = inject(SelectComponent);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  ngOnInit() {
    of(COUNTIES)
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
        this.selectComponent.options.set(res);
        this.changeDetectorRef.markForCheck();
      });
  }
}
