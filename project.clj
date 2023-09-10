(defproject clj_bun_spreader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [compojure "1.6.3"]
                 [cheshire "5.11.0"]
                 [metosin/reitit "0.7.0-alpha5"]
                 [metosin/reitit-ring "0.7.0-alpha5"]
                 [metosin/malli "0.11.0"]
                 [org.clojure/tools.logging "1.2.4"]
                 [nrepl "1.0.0"]
                 ]
  :repl-options {:init-ns clj-bun-spreader.core}
  :main clj-bun-spreader.core)
