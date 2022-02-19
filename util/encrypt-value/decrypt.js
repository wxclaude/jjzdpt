var CryptoJS = require('crypto-js')
module.exports = function (code, secret) {
  return CryptoJS.AES.decrypt(code, secret).toString(CryptoJS.enc.Utf8)
}
