

multipart-producer exposes a web endpoint that produces multipart-mixed content.  The content provided is simply an 'echo' of the query parameters provided.

The base url is:
/multipart-producer/TestServlet

Special query parameters:
separate=<anything really> # Produces a seperate multipart section for each query parameter provided


Example GET urls:
http://localhost:8080/multipart-producer/TestServlet&test=wooohooo&separate=true&

  Produces:
```  
  --11223344556677889900
  Content-Type: application/json
  {"test":["wooohooo"]}
  --11223344556677889900
  Content-Type: application/json
  {"separate":["true"]}
  --11223344556677889900--
```

http://localhost:8080/multipart-producer/TestServlet&test=wooohooo&test2=true&

  Produces:
```
--11223344556677889900
Content-Type: application/json
{"test":["wooohooo"],"test2":["true"]}
--11223344556677889900--
```
