import { jest } from '@jest/globals';

Object.defineProperty(document, 'doctype', {
  value: '<!DOCTYPE html>',
});

/**
 * ISSUE: https://github.com/angular/material2/issues/7101
 * Workaround for JSDOM missing transform property
 */
Object.defineProperty(document.body.style, 'transform', {
  value: () => {
    return {
      enumerable: true,
      configurable: true,
    };
  },
});
// mock implementation for browser crypto
// JSDOM missing subtle property for crypto
import crypto from 'crypto';
Object.defineProperty(global.self, 'crypto', {
  value: {
    subtle: crypto.webcrypto.subtle,
  },
});

Object.defineProperty(global.self, 'structuredClone', { value: (v) => JSON.parse(JSON.stringify(v)) });

Object.defineProperty(global.self, 'fetch', {
  value: jest.fn(() =>
    Promise.resolve({
      text: () => Promise.resolve('some text'),
      json: () => Promise.resolve({ value: 'some text' }),
    }),
  ),
});

HTMLCanvasElement.prototype.getContext = jest.fn() as typeof HTMLCanvasElement.prototype.getContext;
HTMLDialogElement.prototype.show = jest.fn(function () {
  this.open = true;
});
HTMLDialogElement.prototype.close = jest.fn(function () {
  this.open = false;
});
HTMLDialogElement.prototype.showModal = HTMLDialogElement.prototype.show;
