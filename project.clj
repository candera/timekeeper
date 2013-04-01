 (defproject org.craigandera/timekeeper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :launch4j-config-file "l4j.xml"
  :main timekeeper.main
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.2"]]
                   :source-paths ["dev"]}})
