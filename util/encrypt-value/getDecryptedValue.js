var getEnvName = require('./getEnvName')
var decrypt = require('./decrypt')

module.exports = function(code, envName) {
  return decrypt(code, process.env[envName || getEnvName()])
}
