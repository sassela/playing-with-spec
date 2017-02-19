(ns playing-with-spec.core
  (:require [clojure.spec :as s]))


(def recipe
  {::ingredients [1 :kg "aubergines"
                  20 :ml "soy sauce"]
   ::steps ["fry aubergines"
            "add soy sauce"]})

(comment
  ; map-of doesn't tell you about specific keys
  (s/def ::recipe (s/map-of keyword? vector?)))

(s/def ::recipe (s/keys :req [::ingredients]
                        :opt [::steps]))
;;with keysm spec will either look for the keys in a map, or a spec with the same keyword
(s/def ::steps (s/coll-of string?))
(s/def ::ingredients (s/* ::ingredient))
(s/def ::ingredient (s/cat :amount number?
                           :unit keyword?
                           :name string?))

(defn cook!
  [recipe]
  (if-not (s/valid? ::recipe recipe)
    (throw (ex-info (s/explain-str ::recipe recipe)
                    (s/explain-data ::recipe recipe)))
    "enjoy your meal, you culinary goddess."))

(comment
  ; conform converts valid coll to map for easier processing
  ; concat any number of preds/patterns in specfic order
  (s/conform (s/cat :amount number?
                    :unit keyword?
                    :name string?)
             [1 :b "c"])
  => {:amount 1, :unit :b, :name "c"})

(comment
  ; alt equiv to regex | a choice  of one amongst multiple preds
  (s/conform (s/alt :num number?
                    :key keyword?)
             [4])
  => [:num 4])

(comment
  ; explain fns are pretty nice too
  (s/explain ::recipe {})
  val: {} fails spec: :playing-with-spec.core/recipe predicate: (contains? % :playing-with-spec.core/ingredients)
  => nil
  (s/explain-str ::recipe {})
  => "val: {} fails spec: :playing-with-spec.core/recipe predicate: (contains? % :playing-with-spec.core/ingredients)\n"
  (s/explain-data ::recipe {})
  => #:clojure.spec{:problems ({:path [], :pred (contains? % :playing-with-spec.core/ingredients), :val {}, :via [:playing-with-spec.core/recipe], :in []})})

