;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns cambium.codec
  (:require
    [cheshire.core :as cheshire]))


(defn- as-str
  "Turn given argument into string."
  ^String [^Object x]
  (cond
    (instance?
      clojure.lang.Named x) (if-some [^String the-ns (namespace x)]
                              (let [^StringBuilder sb (StringBuilder. the-ns)]
                                (.append sb \/)
                                (.append sb (name x))
                                (.toString sb))
                              (name x))
    (instance? String x)    x
    (nil? x)                ""
    :otherwise              (.toString x)))


;; ----- below are parts of the contract -----


(def ^:const nested-nav?
  "Boolean value - whether this codec supports nested (navigation of) log attributes. This codec sets it to true."
  true)


(defn stringify-key
  "Arity-1 fn to convert MDC key into a string. This codec carries out a plain string conversion."
  ^String [x]
  (as-str x))


(defn stringify-val
  "Arity-1 fn to convert MDC value into a string. This codec encodes the value using Cheshire, falling back to simple
  string conversion on encoding error."
  ^String [x]
  (try
    (cheshire/generate-string x)
    (catch Exception e
      (try
        (cheshire/generate-string (str x))
        (catch Exception e
          "\"Unable to encode MDC value as JSON\"")))))


(defn destringify-val
  "Arity-1 fn to convert MDC string back to original value. This codec decodes the string using Cheshire, falling back
  to returning the original string on parsing error."
  [^String x]
  (try
    (cheshire/parse-string x)
    (catch Exception e
      x)))
