(ns assert-clj.api-test
  (:require [assert-clj.api :as api]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])
  (:import [org.assertj.core.api Assert]))

(def some-actual (gen/return (gen/sample gen/any 1)))

(defn is-assertj-assert? [a] (is (instance? Assert a)))

(defn has-description-text
  [txt a]
  (is (= txt (.descriptionText a))))

(deftest assert-that-creates-assertj-assertion
  (testing "without calling any assert* methods"
    (tc/quick-check
      10
      (prop/for-all [v gen/any]
                    (is-assertj-assert? (api/assert-that v)))))

  (testing "with calling some assert* methods"
    (tc/quick-check
      10
      (prop/for-all [v gen/any]
                    (is (thrown? AssertionError
                                 (api/assert-that v
                                                  (isNotEqualTo v)))))))

  (testing "with calling hyphenized assert* methods"
    (tc/quick-check
      10
      (prop/for-all [v gen/any]
                    (is (thrown? AssertionError
                                 (api/assert-that v
                                                  (is-not-equal-to v))))))))

(deftest assert-that-allows-to-add-a-docstring-right-after-the-actual
  (testing "a simple docstring"
    (tc/quick-check
      10
      (prop/for-all [v gen/any]
                    (has-description-text "Hello World"
                                          (api/assert-that v "Hello World")))))

  (testing "storage of docstring in vars"
    (tc/quick-check
      10
      (prop/for-all [v gen/any
                     s gen/string-ascii]
                    (has-description-text s (api/assert-that v s)))))

  (testing "failure if var does not contain string"
    (tc/quick-check
      10
      (prop/for-all [v gen/any
                     s (gen/such-that (comp not string?) gen/any)]
                    (is
                      (thrown? IllegalArgumentException
                               (api/assert-that v s))))))

  (testing "vector of format string with args"
    (tc/quick-check
      10
      (prop/for-all [v gen/any
                     s1 gen/string-ascii
                     s2 gen/string-ascii]
                    (has-description-text
                      (format "%s-%s" s1 s2)
                      (api/assert-that v ["%s-%s" s1 s2]))))))
