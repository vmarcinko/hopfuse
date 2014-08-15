(ns hopfuse.rpc
  (:require-macros
    [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin]
   [tailrecursion.castra :refer [mkremote]]))

(defn log-to-console [& args]
      (.log js/console (apply str args)))

(defc state {})
(defc error nil)
(defc loading [])

(defc= logged-in? (:logged-user state))
(defc= loading-in-process? (seq loading))

(def login! (mkremote 'hopfuse.api/login! state error loading))
(def logout! (mkremote 'hopfuse.api/logout! state error loading))
(def remove-user! (mkremote 'hopfuse.api/remove-user! state error loading))
(def update-user! (mkremote 'hopfuse.api/update-user! state error loading))
(def get-state (mkremote 'hopfuse.api/get-state state error loading))

(defn init []
  (get-state))
