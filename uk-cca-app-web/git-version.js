const { gitDescribeSync } = require('git-describe');
const { execSync } = require('child_process');
const { version } = require('./package.json');
const { resolve, relative } = require('path');
const { writeFileSync, existsSync, mkdirSync } = require('fs-extra');

const gitInfo = gitDescribeSync({
  dirtyMark: false,
  dirtySemver: false,
});

gitInfo.devVersion = version;
gitInfo.branch = currentBranch();

// Same as git-branch: branch name, or null on a detached HEAD (e.g. CI checkouts).
function currentBranch() {
  try {
    return execSync('git symbolic-ref --short -q HEAD', { cwd: __dirname, encoding: 'utf-8' }).trim();
  } catch {
    return null;
  }
}

if (!existsSync(__dirname + '/src/environments')) {
  mkdirSync(__dirname + '/src/environments');
}
const file = resolve(__dirname, 'src', 'environments', 'version.ts');
writeFileSync(
  file,
  `// IMPORTANT: THIS FILE IS AUTO GENERATED! DO NOT MANUALLY EDIT OR CHECKIN!
/* tslint:disable */
export const VERSION = ${JSON.stringify(gitInfo, null, 4)};
/* tslint:enable */
`,
  { encoding: 'utf-8' },
);

console.log(`Wrote version info ${gitInfo.raw} to ${relative(resolve(__dirname, '..'), file)}`);
