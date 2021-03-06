console.log('Loading Server');
var fs = require('fs');
var express = require('express');
var mongoDao = require('./mongoDao');
var mysqlDao = require('./mysqlDao');
//var fsdbDao = require('./fsdbDao');
//var cassandraDao = require('./cassandraDao');
//var redisDao = require('./redisDao');
var app = express();

//modules below are express middleware
var bodyParser = require('body-parser');
var logger = require('morgan');
var compression = require('compression');
var favicon = require('serve-favicon');

//var http = require('http').Server(app);
//var io = require('socket.io')(http);

var allowCrossDomain = function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');

    next();
};

app.use(bodyParser.json());
app.use(logger('dev'));
app.use(compression());
app.use(allowCrossDomain);

app.use('/', mongoDao);
app.use('/', mysqlDao);
//app.use('/', fsdbDao);
//app.use('/', cassandraDao);
//app.use('/', redisDao);

//websockets stuff
/*

io.on('connection', function(socket){
    console.log('a user connected');
    socket.on('disconnect', function(){
        console.log('user disconnected');
    });
});

*/

//traditional webserver stuff for serving static files
var WEB = __dirname + '/web';
app.use(favicon(WEB + '/favicon.ico'));
app.use(express.static(WEB, {maxAge: '12h'}));
app.use(express.static(__dirname + '/socket.io', {maxAge: '12h'}));
app.get('*', function(req, res) {
    res.header('Access-Control-Allow-Origin', '*');
    res.status(404).sendFile(WEB + '/github404.png');
});

var port = process.env.port || 8080;
var server = app.listen(port);

function gracefulShutdown(){
    console.log('\nStarting Shutdown');
    server.close(function(){
        console.log('Shutdown complete.');
    });
}

process.on('SIGTERM', function(){
    gracefulShutdown();
});

process.on('SIGINT', function(){
    gracefulShutdown();
});

console.log(`Listening on port ${port}`);