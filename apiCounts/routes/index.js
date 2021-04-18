var express = require('express');
const dbconfig = require('../util/dbconfig.js');
var router = express.Router();
var dbConfig = require('../util/dbconfig.js')
var cateController = require('../controllers/cateController');
/* GET home page. */
router.get('/', function(req, res) {
  cateController.getChannels(req,res);
});

module.exports = router;
