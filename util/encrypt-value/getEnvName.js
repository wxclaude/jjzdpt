var path = require('path')
var os = require('os')
var readPkgUp = require('read-pkg-up')
var normalizeName = require('normalize-name')

module.exports = function (dir) {
  dir = dir || path.dirname(process.argv[1])
  var pkg = readPkgUp.sync({
    cwd: dir,
  })
  var name = pkg && pkg.package.name || path.basename(dir) || path.basename(os.homedir())
  return normalizeName(name + '_AES').toUpperCase()
}
