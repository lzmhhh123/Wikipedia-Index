var express = require('express');
var path = require('path');
var fs = require('fs');
var router = express.Router();
var bodyParser = require('body-parser');

var app = express();
var index = new Object();
var idOffset = new Object();
var wikipediaPath = "";

app.use(bodyParser.json());

fs.readFile("./IdOffset", 'utf8', (err, data) => {
  if (err) {
    throw err;
  }
  data = data.split('\n');
  for (let i = 0; i < data.length - 1; ++i) {
    let array = data[i].split(':');
    let id = array[0].split('>')[1];
    idOffset[id] = {
      'offset': parseInt(array[1].split(' ')[0], 10),
      'length': parseInt(array[1].split(' ')[1], 10),
    }
  }
});

fs.readFile("./part-r-00000", 'utf8', (err, data) => {
  if (err) {
    throw err;
  }
  data = data.split('\n');
  for (let i = 0; i < data.length - 1; ++i) {
    let array = data[i].split('\t');
    if (index[array[1]] === undefined) {
      index[array[1]] = [];
    }
    index[array[1]] = index[array[1]].concat([array[0]]);
  }
})

fs.readFile("./WikipediaPath", 'utf8', (err, data) => {
  wikipediaPath = data;
})


router.post('/search', (req, res) => {
  let { value } = req.body;
  res.send({table: index[value]});
})

router.get(`/page/:id`, (req, res) => {
  let pathname = req.originalUrl;
  let id = pathname.split('/')[2];
  if (idOffset[id] === undefined) {
    res.status(404).send("Not Found!");
  } else {
    fs.open(wikipediaPath, 'r', (err, fd) => {
      if (err) res.status(500).send(err);
      let buffer = new Buffer(1000000);
      fs.read(fd, buffer, 0, parseInt(idOffset[id].length) / 4, parseInt(idOffset[id].offset) / 4, (err, length) => {
        res.status(200).send(buffer.toString());
      })
    })
  }
})

app.use('/', router);

app.use('/', express.static(path.join(__dirname, 'build')));

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
