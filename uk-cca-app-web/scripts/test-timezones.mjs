import { spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';

const cwd = fileURLToPath(new URL('..', import.meta.url));
const cli = fileURLToPath(new URL('../node_modules/@angular/cli/bin/ng.js', import.meta.url));
// Start each suite with a real process timezone; changing TZ inside worker-thread specs has no effect on Date.
const timezones = ['UTC', 'Europe/London', 'America/New_York', 'Asia/Tokyo'];
const projects = ['govuk-components', 'common', 'uk-cca-app-web'];
const failures = [];

for (const timezone of timezones) {
  for (const project of projects) {
    console.log(`Running ${project} tests in ${timezone}`);
    const result = spawnSync(process.execPath, [cli, 'test', project, '--no-watch'], {
      cwd,
      env: { ...process.env, TZ: timezone, NO_COLOR: '1', UPDATE_SNAPSHOTS: 'false' },
      stdio: 'inherit',
    });

    if (result.status !== 0) {
      failures.push(`${project} (${timezone})`);
      if (result.error) console.error(result.error);
    }
  }
}

if (failures.length) {
  console.error(`Failed timezone suites: ${failures.join(', ')}`);
  process.exitCode = 1;
} else {
  console.log(`All test suites passed in ${timezones.join(', ')}.`);
}
