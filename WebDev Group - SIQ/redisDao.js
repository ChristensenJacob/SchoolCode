var express = require('express');
var redis = require("redis"),
    client = redis.createClient({host:'172.17.0.5'});

var router = express.Router();

client.on("error", function (err) {
    console.log("Error " + err);
});

//REST API V4 calls go here.
// List
router.get('/api/v4/entries.json', function(req, res) {

});

// Create
router.post('/api/v4/entries.json', function(req, res){

});

// Read
router.get('/api/v4/entries/:id.json', function(req, res){

});

// Update
router.put('/api/v4/entries/:id.json', function(req, res){

});

// Delete
router.delete('/api/v4/entries/:id', function(req, res){
});

module.exports = router;
// END API V4 METHODS
