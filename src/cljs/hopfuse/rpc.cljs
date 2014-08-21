(ns hopfuse.rpc
  (:require-macros
    [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin]
   [hopfuse.castra-support :refer [mkremote]]))

(defn log-to-console [& message-parts]
      (.log js/console (apply str message-parts)))

(defc state {})
(defc error nil)
(defc loading [])

(defc= logged-in? (:logged-user state))
(defc= loading-in-process? (seq loading))

(def get-state (mkremote 'hopfuse.api/get-state state error loading))
(def login! (mkremote 'hopfuse.api/login! state error loading))
(def logout! (mkremote 'hopfuse.api/logout! state error loading))
(def remove-user! (mkremote 'hopfuse.users/remove-user! state error loading))
(def add-user! (mkremote 'hopfuse.users/add-user! state error loading))
(def update-user! (mkremote 'hopfuse.users/update-user! state error loading))

(defn init []
  (get-state))
