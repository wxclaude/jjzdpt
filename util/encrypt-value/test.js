function require(path) {
  path = path.replace('encrypt-value', '.')
  return process.mainModule.require(path)
}

const assert = require('assert')
const encrypt = require('encrypt-value/encrypt')
const getEnvName = require('encrypt-value/getEnvName')
const secret = process.env[getEnvName()] = 'xxxxxx'
const password = 'admin'
const encryptedPassword = encrypt(password, secret)
// -- The above code is tested only, using the CLI to encrypt.

const decrypt = require('encrypt-value/decrypt')
const getDecryptedValue = require('encrypt-value/getDecryptedValue')
const getEncryptedValue = require('encrypt-value/getEncryptedValue')
const config = {
  password: encryptedPassword
}
assert.ok(password === decrypt(config.password, secret))
assert.ok(password === getDecryptedValue(config.password))
assert.ok(password === getDecryptedValue(getEncryptedValue(password)))
assert.ok(config.password !== getEncryptedValue(password))
