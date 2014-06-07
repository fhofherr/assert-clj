(defproject assert-clj "0.1.0-SNAPSHOT"
  :description "A thin wrapper around AssertJ."
  :url "http://github.com/fhofherr/assert-clj"
  :license {:name "Apache v2 License"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.assertj/assertj-core "1.6.1"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/test.check "0.5.8"]
                                  [org.clojure/tools.namespace "0.2.4"]]}})
