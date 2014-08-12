(ns hopfuse.api
  (:require [tailrecursion.castra :as castra]
            [hopfuse.users :as users]))

(defn get-logged-user-from-session []
  (:logged-user @castra/*session*))

(defn store-logged-user-in-session! [user]
  (swap! castra/*session* #(assoc % :logged-user user)))

(castra/defrpc get-state []
               (when-let [logged-user (get-logged-user-from-session)]
                 {:logged-user logged-user
                  :users (users/find-all)}))

(castra/defrpc login! [username password]
               (let [user (users/find-by-username username)]
                 (if (and user (= (:password user) password))
                     (store-logged-user-in-session! user)
                     (throw (castra/ex castra/auth "Username or password incorrect" {:some "data"}))))
               (get-state))

(castra/defrpc logout! []
               (store-logged-user-in-session! nil)
               (get-state))
