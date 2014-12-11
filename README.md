

multipart-producer exposes a web endpoint that produces multipart-mixed content.  The content provided is simply an 'echo' of the query parameters provided.

The base url is:
/multipart-producer/TestServlet

Special query parameters:
separate=<anything really> # Produces a seperate multipart section for each query parameter provided


Example GET urls:

seperate example:
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

together example:
http://localhost:8080/multipart-producer/TestServlet&test=wooohooo&test2=true&

  Produces:
```
  --11223344556677889900
  Content-Type: application/json
  {"test":["wooohooo"],"test2":["true"]}
  --11223344556677889900--
```

array example:
http://localhost:8080/multipart-producer/TestServlet?echo=awesome&array=1&array=2

  Produces:
```
  --11223344556677889900
  Content-Type: application/json
  {"echo":["awesome"],"array":["1","2"]}
--11223344556677889900--
```
