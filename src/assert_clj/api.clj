(ns assert-clj.api
  (:import [org.assertj.core.api Assertions]))

(defn handle-docstring
  [[a & as :as assertions]]
  (letfn [(emit-described-as [s]
            `(~(quote describedAs) ~s (make-array String 0)))

          (emit-cant-resolve [s] 
            `(throw (IllegalArgumentException.
                      (format "'%s' does not resolve to a string!" ~s))))

          (resolve-docstring []
            (cond
              (string? a) (emit-described-as a)
              (symbol? a) (emit-described-as
                            `(if (string? ~a)
                               ~a
                               ~(emit-cant-resolve a)))
              :else a))]
    `(~(resolve-docstring)
      ~@as)))

(defn transform-assertions
  [assertions]
  (-> assertions
      (handle-docstring)))

(defmacro assert-that
  [actual & assertions]
  (if (empty? assertions)
    `(Assertions/assertThat ~actual)
    `(.. (Assertions/assertThat ~actual)
       ~@(transform-assertions assertions))))
