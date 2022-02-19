# encrypt-value

Encrypt and decrypt a property value by an environment variable (e.g. database password).

## Installation

```sh
$ npm i encrypt-value -g
```

## Usage

1. Append `export <PACKAGE>_AES="<secret>"` in `~/.bashrc` or `~/.bash_profile`, and **source** it.

2. Run `npx encrypt-value` in terminal to encrypt your value, and get a ciphertext.

3. Copy the ciphertext to your config file(e.g. `{"password": "<ciphertext>"}`).

4. Get the value by `getDecryptedValue(config.password [, <env_name>])` in your program.

## Node.js

```js
const assert = require("assert");
const encrypt = require("encrypt-value/encrypt");
const getEnvName = require("encrypt-value/getEnvName");
const secret = (process.env[getEnvName()] = "xxxxxx");
const password = "admin";
const encryptedPassword = encrypt(password, secret);
// -- The above code is tested only, using the CLI to encrypt.

const decrypt = require("encrypt-value/decrypt");
const getDecryptedValue = require("encrypt-value/getDecryptedValue");
const getEncryptedValue = require("encrypt-value/getEncryptedValue");
const config = {
  password: encryptedPassword
};
assert.ok(password === decrypt(config.password, secret));
assert.ok(password === getDecryptedValue(config.password));
assert.ok(password === getDecryptedValue(getEncryptedValue(password)));
assert.ok(config.password !== getEncryptedValue(password));
```

## CLI

```sh
# enter the interactive environment
encrypt-value ./my_package

# specify an environment variable name
encrypt-value -N MY_PACKAGE_AES

# enter a secret key forcibly
encrypt-value -S ./my_package

# print the default environment variable name
encrypt-value -E ./my_package

# decrypt a ciphertext
encrypt-value -C <ciphertext> ./my_package

# print help
encrypt-value -h

# print all env with a "_AES" suffix
env|grep _AES
```

## API

### `getEnvName(dir?)`

Return a default environment variable name. (Call a sync function in inside)

### `getEncryptedValue(value, envName?)`

### `getDecryptedValue(code, envName?)`

Encrypt and decrypt a value by a secret key in environment variable.

### `encrypt(value, secret)`

### `decrypt(code, secret)`

Encrypt and decrypt a value by a secret key.

## License

MIT
