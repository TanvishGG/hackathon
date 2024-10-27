const express = require('express')
const app = express()
const { Pool } = require('pg')
const pool = new Pool({
  host: "",
  port: "5432",
  ssl: true,
  user: "",
  password: "",
  database: ""
})
pool.connect().then(() => {
  console.log(`[DATABASE] Database Connected`);
}).catch((err) => {
  console.log(`[DATABASE] Database Connection Failed:`, err)

})
pool.on('error', (err) => {
  console.log(`[DATABASE] Database Error:`, err)
})
app.get('/', (req, res) => {
    res.send('Hello World')
});

app.get('/transport', async (req, res) => { 
   let data = (await pool.query(`SELECT * FROM transport`)).rows;
   data = data.filter(x => x.timestamp > Math.floor((Date.now() - 1000*60*60*2)/1000)).sort((a,b) => a.timestamp - b.timestamp);
   return res.json(data);
})
app.post('/transport', async (req, res) => {
    let { name, mobile,source,destination,timestamp } = req.query;
    if(!name || !mobile || !source || !destination || !timestamp) return res.status(400).send({error: 'Invalid Parameters'});
    await pool.query(`INSERT INTO transport (name, mobile, source, destination, timestamp) VALUES ($1,$2,$3,$4,$5)`,[name,mobile,source,destination,Math.floor(timestamp/1000)]);
    return res.send({success: true});
})
app.delete(`/transport`, async (req, res) => {
  await pool.query(`TRUNCATE TABLE transport`);
  console.log(`[DATABASE] Table Cleared`);
  return res.send({success: true});
})


app.listen(8080, () => {
    console.log('[SERVER] Server Connected')
 })

 process.on('unhandledRejection', (reason, promise) => {
  console.log('[ERROR] Unhandled Rejection at:', reason.stack || reason)
})

process.on('uncaughtException', (error) => {  
  console.log('[ERROR] Uncaught Exception:', error.stack || error)
})

