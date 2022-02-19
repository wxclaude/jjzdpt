var CryptoJS = require('crypto-js')
module.exports = function (value, secret) {
  return CryptoJS.AES.encrypt(value, secret).toString()
}
