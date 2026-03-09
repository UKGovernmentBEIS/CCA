const yaml = require('js-yaml');
const fs = require('fs');
const path = require('path');

(async () => {
  const swaggerFile = path.join(__dirname, 'projects', 'cca-api', 'src', 'assets', 'swagger.yaml');
  const directory = path.dirname(swaggerFile);

  try {
    if (!fs.existsSync(directory)) {
      console.log(`Directory does not exist. Creating directory: ${directory}`);
      fs.mkdirSync(directory, { recursive: true });
    }

    const data = await fetch('http://localhost:8082/api/v3/api-docs');

    if (!data.ok) {
      throw new Error(`Failed to fetch API documentation. HTTP Status: ${data.status}`);
    }

    let json;
    try {
      json = await data.json();
    } catch (jsonError) {
      throw new Error(`Failed to parse JSON from API response: ${jsonError.message}`);
    }

    const yml = yaml.dump(json);

    try {
      fs.writeFileSync(swaggerFile, yml);
      console.log(`Swagger file successfully updated at: ${swaggerFile}`);
    } catch (fsError) {
      throw new Error(`Failed to write the swagger file: ${fsError.message}`);
    }
  } catch (e) {
    console.error('Error occurred:', e.message);
    console.error('COULD NOT UPDATE SWAGGER FILE - CHECK THAT API IS UP AND RUNNING');
    process.exit(1);
  }
})();
