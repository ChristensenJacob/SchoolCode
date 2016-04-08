/**
 * Created by Jacob on 2/23/2016.
 */

/* COMMAND LINE OPTIONS
 * -d <Database Location> // Indicates the database to use
 * -p <Port Number> // Uses a different port number
 * -c <Config File Location> // Uses a different configuration file
 * -u // Prints a usage screen
 */

console.log('Starting EvenStuck...');

// node.js variables
var fs = require('fs');
var express = require('express');
var _ = require('underscore');

//modules below are express middleware
var bodyParser = require('body-parser');
var logger = require('morgan');
var compression = require('compression');
var argv = require('minimist')(process.argv.slice(2));
var sqlite3 = require('sqlite3').verbose();
var oauthserver = require('oauth2-server');

var app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(logger('dev'));
app.use(compression());

var allowCrossDomain = function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');

    next();
};

// argument validation
if (argv.u) {
    usageScreen();
    process.exit(0);
}
if (isNaN(argv.p || 0) || !argv.d) {
    usageScreen();
    process.exit(1);
}

// app variables
var configFile = __dirname + '/evenstuck.json';
var config = JSON.parse(fs.readFileSync(argv.c || configFile));

//SQLite Stuff
console.log('Connecting to Database');
var db = new sqlite3.Database(argv.d || config.database ||  '../Evennia/CraterStuck/server/evennia.db3');

/*
// OAUTH2 Stuff
app.oauth = oauthserver({
    model: {}, // See below for specification
    grants: ['password'],
    debug: true
});

app.all('/oauth/token', app.oauth.grant());

app.get('/', app.oauth.authorise(), function (req, res) {
    res.send('Secret area');
});

app.use(app.oauth.errorHandler());
*/

//REST API calls go here
app['get']('/api', function (req, res) {
    res.json({response: 'EvenStuck API responding'});
});

app['get']('/api/v1/allusers', function (req, res) {
    db.all("SELECT db_key AS name FROM objects_objectdb WHERE db_typeclass_path IS 'typeclasses.characters.Character'", function(err,rows){
        res.json(rows);
    });
});

app['get']('/api/v1/activeusers', function (req, res) {
    db.all("SELECT db_key AS name, db_location_id AS room FROM objects_objectdb WHERE db_player_id IS NOT NULL", function(err,rows){
        res.json(rows);
    });
});

app['get']('/api/v1/description/:id', function (req, res) {
    var id = req.params.id;
    if (isNaN(id)) {
        res.sendStatus(400);
    }
    else {
        db.all(`SELECT db_key AS description FROM objects_objectdb WHERE id IS ${id}`, function(err,rows){
            res.json(rows);
        });
    }
});

/*
app['post']('/api/v1/CSProbed/:id', function (req, res) {
    var id = req.params.id;
    var input = req.toJSON();
    if (input.key != KEY_CSPROBED)
        res.sendStatus(304);
    if (isNaN(input.val) || isNaN(id))
        res.sendStatus(400);
    var oldval = 0;
    db.all(`SELECT suitpower AS val FROM objects_objectdb_db_attributes,typeclasses_attribute WHERE objectdb_id IS '${id}'`, function(err,rows){
        oldval = parseInt(rows.toJSON().val);
    });
    oldval += input.val;
    if (oldval > 100)
        oldval = 100;
    db.run(`UPDATE objects_objectdb_db_attributes,typeclasses_attribute SET suitpower = ${oldval} WHERE objectdb_id IS '${id}'`);

});
*/

//traditional webserver stuff for serving static files
var WEB = __dirname + '/web';
app.use(express.static(WEB, {maxAge:'30s'}));
app.get('*', function(req, res) {
    res.header('Access-Control-Allow-Origin', '*');
    res.sendStatus(404);
});

var port = argv.p || config.port || process.env.port || 45500;
var server = app.listen(port);

function usageScreen() {
    console.log('Usage: node evenstuck.js -d <Database Location> [-p <portnumber>, -c <configfile>, -u]');
}

function gracefulShutdown() {
    console.log('Starting shutdown...');
    server.close(function(){
        db.close();
        console.log('Shutdown complete');
    });
}

process.on('SIGTERM', function(){
    gracefulShutdown();
});

process.on('SIGINT', function(){
    gracefulShutdown();
});

console.log(`EvenStuck ready on port ${port}`);