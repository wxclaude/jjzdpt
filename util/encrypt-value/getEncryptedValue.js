var getEnvName = require('./getEnvName')
var encrypt = require('./encrypt')

module.exports = function(value, envName) {
  return encrypt(value, process.env[envName || getEnvName()])
}
