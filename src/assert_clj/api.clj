(ns assert-clj.api
  (:import [org.assertj.core.api Assertions]))

(defmacro assert-that
  [actual & assertions]
  (if (empty? assertions)
    `(Assertions/assertThat ~actual)
    `(.. (Assertions/assertThat ~actual)
       ~@assertions)))
