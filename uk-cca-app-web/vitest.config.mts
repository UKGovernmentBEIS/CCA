import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'jsdom',
    setupFiles: ['src/test-setup'],
    // TODO: revert this `update` config as it is a temp workaround to enable snapshot updating until
    // the Angular CLI supports passing the --update flag to Vitest
    update: process.env.UPDATE_SNAPSHOTS === 'true',
    globals: true,
    clearMocks: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html', 'lcov'],
      reportsDirectory: './coverage',
      exclude: [
        '**/node_modules/**',
        '**/dist/**',
        '**/coverage/**',
        '**/testing/**',
        '**/*.spec.ts',
        '**/*mock*.ts',
        '**/main.ts',
        '**/environments/**',
      ],
    },
  },
});
