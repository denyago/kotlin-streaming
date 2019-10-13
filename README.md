![CI Status](https://github.com/denyago/kotlin-streaming/workflows/testCI/badge.svg)

# Streaming JSON with Kotlin, KTor and \[Klaxson or Moshi\]

Idea: `source` reads User records from a DB. Millions of them. 
`proxy` pulls those records through and adds Fortunes to each of them.
`client` fetches that stream of Users with Fortunes and just prints out.

APIs:
```json5
// GET /source/users/stream?start=1&number=1000000
[
  {id: 1, name: "Jane"},
  {id: 2, name: "Fred"},
  // 999997 users
  {id: 1000000, name: "Sam"}
]
```
```json5

// GET /proxy/users_with_fortunes/stream?start=1&number=1000000

[
  {id: 1, name: "Jane", fortune: "Some of the things that live the longest in peoples' memories never really happened."},
  // and so on...
]
```

## Run it

Read from source:

```bash
while [[ 1 -le 5 ]]; do curl --no-buffer "http://127.0.0.1:8080/source/users/stream" > /dev/null; done
```

## Status and ToDo

- [ ] Add Source service
    - [x] Skeleton
    - [x] Hard-coded data and infinite Stream
    - [ ] DB
    - [ ] Stream reader from DB
- [ ] Add Proxy service
    - [ ] Skeleton
    - [ ] Fortunes source
    - [ ] Stream proxy
- [ ] Add Client
    - [x] Mock it with Bash
    - [ ] Skeleton
    - [ ] Nice CLI output

## Articles

On streaming JSON processing:
- Moshi: [Advanced JSON parsing techniques using Moshi and Kotlin](https://medium.com/@BladeCoder/advanced-json-parsing-techniques-using-moshi-and-kotlin-daf56a7b963d)
- Klaxon [Processing JSON with Kotlin and Klaxson](https://www.baeldung.com/kotlin-json-klaxson)
