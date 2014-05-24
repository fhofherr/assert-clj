(ns user
  (:require [clojure.test :as t]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [clojure.repl :refer [doc find-doc source]]
            [clojure.java.javadoc :refer [javadoc]]))

(defn run-tests
  []
  (refresh)
  (t/run-tests 'assert-clj.api-test))
