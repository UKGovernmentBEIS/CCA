import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BackToTopComponent } from './back-to-top.component';

describe('BackToTopComponent', () => {
  let fixture: ComponentFixture<HostComponent>;
  let router: Router;

  @Component({
    template: '<main id="main-content" tabindex="-1"><cca-back-to-top /></main>',
    imports: [BackToTopComponent],
  })
  class HostComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HostComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    router = TestBed.inject(Router);
    // `routerLink="[]"` triggers a same-route navigation on click; stub it out
    vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    vi.spyOn(window, 'scroll').mockImplementation(() => undefined);
    fixture = TestBed.createComponent(HostComponent);
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should create', () => {
    expect(fixture).toBeTruthy();
  });

  it('should scroll to the top and move focus to the main content region', () => {
    const scrollSpy = vi.mocked(window.scroll);
    const link = fixture.nativeElement.querySelector('a');
    link.click();

    expect(scrollSpy).toHaveBeenCalledWith({ top: 0, left: 0, behavior: 'smooth' });
    expect(document.activeElement).toBe(fixture.nativeElement.querySelector('#main-content'));
  });
});
