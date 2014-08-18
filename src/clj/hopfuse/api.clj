(ns hopfuse.api
  (:require [tailrecursion.castra :as castra]
            [hopfuse.db-support :as dbsupport]
            [hopfuse.users-data :as usersdata]))

(defn get-logged-user-from-session []
  (:logged-user @castra/*session*))

(defn store-logged-user-in-session! [user]
  (swap! castra/*session* assoc :logged-user user))

(defn logged-in?
  ([]
   (get-logged-user-from-session))
  ([& roles]
   (contains? (apply hash-set roles) (:role (get-logged-user-from-session)))))

(castra/defrpc get-state
               ([]
                (get-state (dbsupport/get-last-db)))
               ([db]
                 (when-let [logged-user (get-logged-user-from-session)]
                  {:logged-user logged-user
                   :users       (usersdata/find-all db)})))

(defn get-state-with-info-message [db & message-parts]
  (assoc (get-state db) :info-message (apply str message-parts)))

(castra/defrpc login! [username password]
               (let [db (dbsupport/get-last-db)
                     user (usersdata/find-by-username db username)]
                 (if (and user (= (:password user) password))
                     (store-logged-user-in-session! user)
                     (throw (castra/ex castra/auth "Username or password incorrect" {:some "data"})))
                 (get-state db)))

(castra/defrpc logout! []
               (store-logged-user-in-session! nil)
               (get-state))
