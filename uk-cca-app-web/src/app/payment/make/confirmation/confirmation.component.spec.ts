import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { PaymentModule } from '../../payment.module';
import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let authStore: AuthStore;

  const keycloakService = mockClass(KeycloakService);

  const activatedRouteBank = { queryParams: of({ method: 'BANK_TRANSFER' }) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, PaymentModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteBank },
        { provide: KeycloakService, useValue: keycloakService },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserProfile({ firstName: 'First', lastName: 'Last' });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
