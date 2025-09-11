import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';

import { BuyOutAndSurplusInfoService } from 'cca-api';

import { mockAuthState } from '../../../../specs/fixtures/mock';
import { BuyoutAndSurplusTabComponent } from './buyout-and-surplus-tab.component';
import { BuyoutAndSurplusTabStore } from './buyout-and-surplus-tab.store';
import { mockBuyoutInfo } from './testing/mock-data';

describe('BuyoutAndSurplusTabComponent', () => {
  let component: BuyoutAndSurplusTabComponent;
  let fixture: ComponentFixture<BuyoutAndSurplusTabComponent>;
  let buyoutAndSurplusTabStore: BuyoutAndSurplusTabStore;
  let authStore: AuthStore;
  let serviceSpy: jest.SpyInstance;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuyoutAndSurplusTabComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        BuyoutAndSurplusTabStore,
        AuthStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: 123 }),
        },
        {
          provide: BuyOutAndSurplusInfoService,
          useValue: {
            getBuyOutSurplusInfoByAccountId: jest.fn(() => of(mockBuyoutInfo)),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BuyoutAndSurplusTabComponent);
    component = fixture.componentInstance;
    buyoutAndSurplusTabStore = TestBed.inject(BuyoutAndSurplusTabStore);
    authStore = TestBed.inject(AuthStore);
    serviceSpy = jest.spyOn(TestBed.inject(BuyOutAndSurplusInfoService), 'getBuyOutSurplusInfoByAccountId');

    authStore.setState(mockAuthState);
    buyoutAndSurplusTabStore.setState(mockBuyoutInfo);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the service to fetch buyout and surplus info', () => {
    expect(serviceSpy).toHaveBeenCalledWith(123);
  });

  //TODO uncomment when change links are added back.
  // it('should display "Change" and "View history" links for REGULATOR', () => {
  //   serviceSpy.mockReturnValue(of(mockBuyoutInfo));
  //   fixture = TestBed.createComponent(BuyoutAndSurplusTabComponent);
  //   fixture.detectChanges();
  //
  //   const links = fixture.nativeElement.querySelectorAll('a.govuk-link');
  //   const linkTexts = Array.from(links).map((a: HTMLAnchorElement) => a.textContent?.trim());
  //
  //   expect(linkTexts).toContain('Change');
  //   expect(linkTexts).toContain('View history');
  // });
  //
  // it('should not display "Change" links if user is SECTOR_USER', () => {
  //   authStore.setState({
  //     ...mockAuthState,
  //     userState: { ...mockAuthState.userState, roleType: 'SECTOR_USER' },
  //   });
  //   fixture.detectChanges();
  //
  //   const links = fixture.nativeElement.querySelectorAll('a.govuk-link');
  //   const linkTexts = Array.from(links).map((a: HTMLAnchorElement) => a.textContent?.trim());
  //
  //   expect(linkTexts).not.toContain('Change');
  //   expect(linkTexts).toContain('View history'); // still shown if hasHistory is true
  // });

  it('should not display "View history" link if hasHistory is false', () => {
    const mockWithoutHistory = {
      ...mockBuyoutInfo,
      surplusGainedDTOList: [
        {
          targetPeriod: 'TP6',
          surplusGained: '1.213',
          hasHistory: false,
        },
      ],
    };

    serviceSpy.mockReturnValue(of(mockWithoutHistory));
    fixture = TestBed.createComponent(BuyoutAndSurplusTabComponent);
    fixture.detectChanges();

    const links = fixture.nativeElement.querySelectorAll('a.govuk-link');
    const linkTexts = Array.from(links).map((a: HTMLAnchorElement) => a.textContent?.trim());

    expect(linkTexts).not.toContain('View history');
  });
});
