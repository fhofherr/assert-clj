(ns assert-clj.api
  "Contains wrappers for classes and methods in
  [org.assertj.core.api](http://joel-costigliola.github.io/assertj/core/api/org/assertj/core/api/package-summary.html)."
  (:import [org.assertj.core.api Assertions]))

(defn- handle-docstring
  [[a & as :as assertions]]
  (letfn [(emit-described-as
            ([s]
             (emit-described-as s []))
            ([s ss]
            `(~(quote describedAs) ~s (into-array String ~(vec ss)))))

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
              (vector? a) (emit-described-as (first a) (rest a))
              :else a))]
    `(~(resolve-docstring)
      ~@as)))

(defn- de-hyphenize-methods
  [assertions]
  (letfn [(de-hyphenize-symbol [s]
            (-> s
                (name)
                (clojure.string/replace #"-\w"
                                    #(.. %
                                         (substring 1)
                                         (toUpperCase)))
                (symbol)))

          (de-hyphenize-form [[x & xs]]
            (cons (de-hyphenize-symbol x) xs))]
    (map de-hyphenize-form assertions)))

(defn- transform-assertions
  [assertions]
  (-> assertions
      (handle-docstring)
      (de-hyphenize-methods)))

(defmacro assert-that
  "Wrapper around `Assertions.assertThat` methods. Expects the `actual` value
  as the first argument and `assertions` as the second. The `assertions` may
  contain any methods of `Assert` or one of its subclasses. The methods may be
  given using hyphenized names instead of camel-cased ones. Additionally a
  docstring may be given directly after the `actual` value.

  Examples:

  ```clojure
  ;; Camel-cased names
  (assert-that 1
               (isNotEqualTo 2))

  ;; Hyphenized names
  (assert-that 1
               (is-not-equal-to 2)

  ;; A plain docstring
  (assert-that  1
                \"is never equal\"
                (is-not-equal-to 2))

  ;; A docstring with formatting
  (assert-that 1
               [\"%s is never equal to %s\" 1 2]
               (is-not-equal-to 2)
  ```"
  [actual & assertions]
  (if (empty? assertions)
    `(Assertions/assertThat ~actual)
    `(.. (Assertions/assertThat ~actual)
       ~@(transform-assertions assertions))))
