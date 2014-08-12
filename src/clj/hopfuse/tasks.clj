(ns hopfuse.tasks
  (:require
    [tailrecursion.boot.core :as boot]))

(boot/deftask nrepl
  "Runs an nrepl server."
  [& [{:keys [port] :or {port 0}}]]
	(boot/set-env! :dependencies '[[org.clojure/tools.nrepl "0.2.3"]])
	(require '[clojure.tools.nrepl.server :refer [start-server]])
	(let [server ((resolve 'start-server) :port port)]
	;      (let [server (start-server :port port)]
	(println "Started nREPL server on port" (:port server))
	(spit ".nrepl-port" (:port server))
	identity))

