(defproject nurikabe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async  "0.2.371"]
                 [org.clojure/math.combinatorics  "0.1.1"]
                 [org.clojure/math.numeric-tower  "0.0.4"]]

  :main ^:skip-aot nurikabe.core
  :target-path "target/%s"
  :profiles  {{ :uberjar {:aot :all}}
              { :dev  {:dependencies  [[midje  "1.6.3"]]
                          :plugins  [[lein-midje  "3.1.3"]]}}}
  )
