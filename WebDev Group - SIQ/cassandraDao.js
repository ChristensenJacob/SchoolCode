var express = require('express');
var cassandra = require('cassandra-driver');
var async = require('async');

var router = express.Router();

//Connect to the cluster
var client = new cassandra.Client({contactPoints: ['172.17.0.2'], keyspace: 'siq'});


//REST API V3 calls go here.
// List
router.get('/api/v3/entries.json', function(req, res) {

});

// Create
router.post('/api/v3/entries.json', function(req, res){

});

// Read
router.get('/api/v3/entries/:id.json', function(req, res){

});

// Update
router.put('/api/v3/entries/:id.json', function(req, res){

});

// Delete
router.delete('/api/v3/entries/:id', function(req, res){
});

module.exports = router;
// END API V3 METHODS
