(ns assert-clj.api-test
  (:require [assert-clj.api :as api]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])
  (:import [org.assertj.core.api Assert]))

(def some-actual (gen/return (gen/sample gen/any 1)))

(defn is-assertj-assert? [a] (instance? Assert a))

(defspec assert-that-creates-assertj-assertion 10
  (prop/for-all [v gen/any]
                (is-assertj-assert? (api/assert-that v))))

(defspec assert-that-allows-to-call-the-assertions-methods 10
  (prop/for-all [v gen/any]
                (api/assert-that v
                                 (isEqualTo v)
                                 (isSameAs v))))

(defspec assert-that-allows-to-add-a-docstring-right-after-the-actual 10
  (prop/for-all [v gen/any]
                 (= "Hello World"
                    (.descriptionText (api/assert-that v "Hello World")))))

(defspec assert-that-allows-to-store-docstrings-in-vars 10
  (prop/for-all [v gen/any
                 s gen/string-ascii]
                (= s (.descriptionText (api/assert-that v s)))))

(deftest assert-that-fails-if-docstring-var-is-no-string
  (tc/quick-check
    10
    (prop/for-all [v gen/any
                   s (gen/such-that (comp not string?) gen/any)]
                  (is (thrown? IllegalArgumentException
                               (api/assert-that v s))))))
