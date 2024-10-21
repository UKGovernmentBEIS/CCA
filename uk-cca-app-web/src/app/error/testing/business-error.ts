import { ChangeDetectionStrategy, Component, NgModule } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { BusinessError } from '../business-error/business-error';
import { BusinessErrorService } from '../business-error/business-error.service';

export const expectBusinessErrorToBe = async (error: BusinessError) => {
  return expect(firstValueFrom(TestBed.inject(BusinessErrorService).error$)).resolves.toEqual(error);
};

@Component({
  standalone: true,
  selector: 'cca-business-error',
  template: '',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BusinessErrorStubComponent {}

@NgModule({
  imports: [
    RouterTestingModule.withRoutes([{ path: 'error/business', component: BusinessErrorStubComponent }]),
    BusinessErrorStubComponent,
  ],
})
export class BusinessTestingModule {}
