(defproject clj_performance_tester "0.1.0-SNAPSHOT"
  :description "Clojure server for comparing against other languages."
  :url "http://github.com/dawguy/blog"
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
                 [com.clojure-goes-fast/clj-async-profiler "1.0.3"]
                 ]
  :jvm-opts ["-Djdk.attach.allowAttachSelf"
             "-XX:+UnlockDiagnosticVMOptions"
             "-XX:+DebugNonSafepoints"
             "-XX:-MaxFDLimit"]
  :repl-options {:init-ns clj-performance-tester.core}
  :main clj-performance-tester.core)
