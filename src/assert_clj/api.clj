(ns assert-clj.api
  (:import [org.assertj.core.api Assertions]))

(defn- handle-docstring
  [[a & as :as assertions]]
  (if (string? a)
    `((~(quote describedAs) ~a (make-array String 0))
      ~@as)
    assertions))

(defn- transform-assertions
  [assertions]
  (-> assertions
      (handle-docstring)))

(defmacro assert-that
  [actual & assertions]
  (if (empty? assertions)
    `(Assertions/assertThat ~actual)
    `(.. (Assertions/assertThat ~actual)
       ~@(transform-assertions assertions))))
