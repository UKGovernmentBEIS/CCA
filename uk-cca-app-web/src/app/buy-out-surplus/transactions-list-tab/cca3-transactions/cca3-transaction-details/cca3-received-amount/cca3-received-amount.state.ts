import { Injectable } from '@angular/core';

import { Cca3ReceivedAmountForm } from './cca3-received-amount-form.provider';

@Injectable()
export class Cca3ReceivedAmountState {
  form: Cca3ReceivedAmountForm | null = null;
}
