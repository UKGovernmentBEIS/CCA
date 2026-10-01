import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router, RouterOutlet } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { MainContentFocusDirective } from './main-content-focus.directive';

describe('MainContentFocusDirective', () => {
  let directive: MainContentFocusDirective;
  let fixture: ComponentFixture<TestComponent>;
  let router: Router;

  @Component({
    template: '<main id="main-content" tabindex="-1"><router-outlet ccaMainContentFocus></router-outlet></main>',
    imports: [MainContentFocusDirective, RouterOutlet],
  })
  class TestComponent {}

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'first', component: TestComponent },
          { path: 'second', component: TestComponent },
        ]),
        TestComponent,
      ],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    router = TestBed.inject(Router);
    directive = fixture.debugElement
      .query(By.directive(MainContentFocusDirective))
      .injector.get(MainContentFocusDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should not move focus on the initial navigation', async () => {
    await router.navigate(['first']);
    expect(fixture.nativeElement.querySelector('#main-content')).not.toEqual(document.activeElement);
  });

  it('should focus the main content region on subsequent navigation', async () => {
    await router.navigate(['first']);
    expect(fixture.nativeElement.querySelector('#main-content')).not.toEqual(document.activeElement);

    await router.navigate(['second']);
    expect(fixture.nativeElement.querySelector('#main-content')).toEqual(document.activeElement);
  });
});
