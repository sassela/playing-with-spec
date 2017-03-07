(ns playing-with-spec.leverage
  (:require [clojure.spec :as s]
            [clojure.spec.test :as test]
            [clojure.string :as str]))

(s/def ::index-of-args (s/cat :source string? :search string?))

(defn my-index-of
  [source search]
  (str/index-of source search))


(comment precise error messages given nested data)
(s/explain (s/every ::index-of-args) [["good" "a"]
                                      ["bad" :b]])
;In: [1 1] val: :b fails spec: :playing-with-spec.leverage/index-of-args at: [:search] predicate: string?

(s/check-asserts true)
(s/assert ::index-of-args ["good" "a"])
(s/assert ::index-of-args ["bad" :b])
;CompilerException clojure.lang.ExceptionInfo: Spec assertion failed ...

(comment fdef defines input/ouput types & relationship between args & ret)
(s/fdef my-index-of
        :args ::index-of-args
        :ret nat-int?
        :fn #(<= (:ret %) (-> % :args :source count)))

(comment fdef evaluation allows enhanced doc...)
(clojure.repl/doc my-index-of)
;playing-with-spec.leverage/my-index-of
;([source search])
;Spec
;args: (cat :source string? :search string?)
;ret: nat-int?
;fn: (<= (:ret %) (-> % :args :source count))

(comment ...gen tests check correct implementation...)
(->> (test/check `my-index-of) test/summarize-results)
;{:spec
;      (fspec
;        :args
;        :playing-with-spec.leverage/index-of-args
;        :ret
;        nat-int?
;        :fn
;        (<= (:ret %) (-> % :args :source count))),
; :sym playing-with-spec.leverage/my-index-of,
; :failure
;      {:clojure.spec/problems
;                               [{:path [:ret], :pred nat-int?, :val nil, :via [], :in []}],
;       :clojure.spec.test/args ("" "0"),
;       :clojure.spec.test/val nil,
;       :clojure.spec/failure :check-failed}}
;=> {:total 1, :check-failed 1}

(comment ...instrument check correct call. Points to precise location in stack trace)
(my-index-of "test" 42)
;ExceptionInfo Call to #'playing-with-spec.leverage/my-index-of did not conform to spec:
;In: [1] val: 42 fails at: [:args :search] predicate: string?
;:clojure.spec/args  ("test" 42)
;:clojure.spec/failure  :instrument
;:clojure.spec.test/caller  {:file "form-init4500404377503465942.clj", :line 1, :var-scope playing-with-spec.leverage/eval5577}
;clojure.core/ex-info (core.clj:4725)

