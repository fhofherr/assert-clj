(ns assert-clj.api
  (:import [org.assertj.core.api Assertions]))

(defn- handle-docstring
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
  [actual & assertions]
  (if (empty? assertions)
    `(Assertions/assertThat ~actual)
    `(.. (Assertions/assertThat ~actual)
       ~@(transform-assertions assertions))))
