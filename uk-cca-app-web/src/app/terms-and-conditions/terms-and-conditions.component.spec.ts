import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { AuthService, LatestTermsStore } from '@shared/services';

import { UsersService } from 'cca-api';

import { TermsAndConditionsComponent } from './terms-and-conditions.component';

describe('TermsAndConditionsComponent', () => {
  let fixture: ComponentFixture<TermsAndConditionsComponent>;
  let httpTestingController: HttpTestingController;
  let latestTermsStore: LatestTermsStore;

  const authService: Partial<jest.Mocked<AuthService>> = {
    loadUserTerms: jest.fn(() => of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TermsAndConditionsComponent],
      providers: [
        UsersService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();

    latestTermsStore = TestBed.inject(LatestTermsStore);
    latestTermsStore.setLatestTerms({ url: '/test', version: 2 });

    httpTestingController = TestBed.inject(HttpTestingController);

    fixture = TestBed.createComponent(TermsAndConditionsComponent);
    fixture.detectChanges();
  });

  afterEach(() => httpTestingController.verify());

  it('should create', () => {
    expect(fixture.nativeElement).toMatchSnapshot('terms-and-conditions ');
  });
});
