#!/usr/bin/env node

var path = require("path");
var inquirer = require("inquirer");
var optimist = require("optimist");
var getEnvName = require("./getEnvName");
var encrypt = require("./encrypt");
var decrypt = require("./decrypt");
var pkg = require("./package");

var argv = optimist
  .usage(pkg.name + " [OPTIONS] [<package_directory>]")
  .options("env-name", {
    alias: "N",
    desc:
      'The environment variable name with a secret key. Default: "<package_name>_AES"'
  })
  .options("secret-key", {
    alias: "S",
    desc: "Enter a secret key forcibly, not from environment variable."
  })
  .options("ciphertext", {
    alias: "C",
    desc: "The ciphertext for decrypt."
  })
  .options("show-env-name", {
    alias: "E",
    desc:
      "Show the default environment variable name for the specified package."
  })
  .options("version", {
    alias: "v",
    desc: "Display the version."
  })
  .options("help", {
    alias: "h",
    desc: "Print this help."
  })
  .boolean(["show-env-name", "version", "help"]).argv;

var packageDir = path.resolve(argv._[0] || ".");
var defaultEnvName = getEnvName(packageDir);

function main() {
  var questions = [];
  var envName = argv["env-name"] || defaultEnvName;
  var secret = process.env[envName];
  var ciphertext = argv["ciphertext"];

  if (argv["enter-secret"] || !secret) {
    questions.push({
      name: "secret",
      type: "password",
      message: "Please enter your secret key:"
    });
  }

  if (ciphertext) {
    if (questions.length) {
      inquirer.prompt(questions).then(
        function (data) {
          console.log(decrypt(ciphertext, data.secret || secret));
        },
        function (err) {
          console.error(err.stack || err);
        }
      );
    } else {
      console.log(decrypt(ciphertext, secret));
    }
  } else {
    questions.push({
      name: "value",
      message: "Please enter a value:"
    });

    inquirer.prompt(questions).then(
      function (data) {
        console.log(encrypt(data.value, data.secret || secret));
      },
      function (err) {
        console.error(err.stack || err);
      }
    );
  }
}

if (argv.version) {
  console.log(pkg.version);
} else if (argv["show-env-name"]) {
  console.log(defaultEnvName);
} else if (argv.help) {
  optimist.showHelp();
} else {
  main();
}
