const axios = require("axios");
const base64 = require("base-64");

const HEALTCHECK_URL = "http://example.com/healthcheck";
const PARAM_VALUE = "http://check.com/healthcheck";
const encodedParam = base64.encode(PARAM_VALUE);
const body = {
  sparqlReqEncoded64: encodedParam,
  ontologyURIEncoded64: encodedParam,
  ontologyContentEncoded64: encodedParam,
  mappingsEncoded64: encodedParam,
};

axios
  .post(HEALTCHECK_URL, body)
  .then((response) => {
    if (response.status === 200) {
      console.log("Healthcheck sent successfully");
    } else {
      console.log(
        `Failed to send healthcheck. Status code: ${response.status}`
      );
    }
  })
  .catch((error) => {
    console.error(`An error occurred: ${error.message}`);
  });
