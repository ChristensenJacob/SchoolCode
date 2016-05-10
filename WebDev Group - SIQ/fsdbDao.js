var express = require('express');
var fs = require('fs');

var router = express.Router();


//REST API V0 calls go here.
// List
router.get('/api/v0/entries.json', function(req, res) {
    fs.readdir('fsdb', function(err,files){
        if (err) throw err;
        var entries = [];
        var i = 0;
        files.forEach(function(entry) {
            fs.readFile('fsdb/' + entry,'utf8',function(err,string){
                if (err) throw err;
                item = JSON.parse(string);
                entries[i++] = {"id":entry.split('.')[0],"subject":item.subject};
            });
        });
        // NEVER DO THIS IN REAL LIFE HOLY SHIT
        // Multiple async operations are hard :(
        setTimeout(function(){res.status(200).json(entries);}, 200);

    });
});

// Create
router.post('/api/v0/entries.json', function(req, res){
    var data = req.data;
    var curr = 0;
    fs.readdir('fsdb', function(err,files){
        if (err) throw err;
        files.forEach(function(entry) {
            next = entry.split('.')[0];
            if (next > curr)
                curr = next;
        });
        fs.writeFile('fsdb/' + ++curr + '.json', data, 'utf8', function(err){
            if (err) throw err;
            res.status(201).json(curr);
        });

    });
});

// Read
router.get('/api/v0/entries/:id.json', function(req, res){
    var data = req.data;
    var id = req.params.id;
    var found = false;
    fs.readdir('fsdb', function(err,files){
        if (err) throw err;
        files.forEach(function(entry) {
            if (id == entry.split('.')[0]) {
                found = true;
                fs.readFile('fsdb/' + entry, 'utf8', function(err,string) {
                    if (err) throw err;
                    res.status(200).json(JSON.parse(string));
                })
            }
        });
        setTimeout(function(){if (!found) res.sendStatus(404);}, 250);

    });

});

// Update
router.put('/api/v0/entries/:id.json', function(req, res){
    var data = req.data;
    var id = req.params.id;
    var found = false;
    fs.readdir('fsdb', function(err,files){
        if (err) throw err;
        files.forEach(function(entry) {
            if (id == entry.split('.')[0]) {
                found = true;
                fs.writeFile('fsdb/' + entry, data, 'utf8', function(err) {
                    if (err) throw err;
                    res.sendStatus(204);
                })
            }
        });
        setTimeout(function(){if (!found) res.sendStatus(404);}, 250);

    });
});

// Delete
router.delete('/api/v0/entries/:id', function(req, res){
    var data = req.data;
    var id = req.params.id;
    var found = false;
    fs.readdir('fsdb', function(err,files){
        if (err) throw err;
        files.forEach(function(entry) {
            if (id == entry.split('.')[0]) {
                found = true;
                fs.unlink('fsdb/' + entry, function(err,string) {
                    if (err) throw err;
                    res.sendStatus(204);
                })
            }
        });
        setTimeout(function(){if (!found) res.sendStatus(404);}, 250);

    });
});

module.exports = router;
// END API V0 METHODS