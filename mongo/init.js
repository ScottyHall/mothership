const password = require('./mongo.pass.js');

db.createUser({
    user: "mothership",
    pwd: password,
    roles: [
        { role: "readWrite", db: "mothershipDb" }
    ]
});

db.createCollection("users");