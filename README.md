# clj_performance_tester

A clojure server designed to test how well Clojure performs with common async tasks.

In this case it is a webserver which will add a small payload to a queue and after a timeout remove the payload from the queue.

## Usage

### Server
Starts the server on port 8889 and opens a nrepl port on port 8888.

`lein run`

### Client
Send requests with payloads via companion client. Each request attempts to clear old payloads and enqueues a new payload.

`go run cmd/pinger.go`

## License

Copyright © 2023 dawguy
