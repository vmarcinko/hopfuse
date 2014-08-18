(ns hopfuse.api
  (:require [tailrecursion.castra :as castra]
            [hopfuse.users :as users]
            [hopfuse.db-support :as dbsupport]))

(defn get-logged-user-from-session []
  (:logged-user @castra/*session*))

(defn store-logged-user-in-session! [user]
  (swap! castra/*session* #(assoc % :logged-user user)))

(castra/defrpc get-state
               ([]
                (get-state (dbsupport/get-last-db)))
               ([db]
                 (when-let [logged-user (get-logged-user-from-session)]
                  {:logged-user logged-user
                   :users       (users/find-all db)})))

(defn get-state-with-info-message [db & msg]
  (assoc (get-state db) :info-message (apply str msg)))

(castra/defrpc login! [username password]
               (Thread/sleep 2000)
               (let [db (dbsupport/get-last-db)
                     user (users/find-by-username db username)]
                 (if (and user (= (:password user) password))
                     (store-logged-user-in-session! user)
                     (throw (castra/ex castra/auth "Username or password incorrect" {:some "data"})))
                 (get-state db)))

(castra/defrpc logout! []
               (store-logged-user-in-session! nil)
               (get-state))

(castra/defrpc remove-user! [id]
               (Thread/sleep 2000)
               (let [tx-report (users/remove-user! id)
                     user (users/find-by-id (:db-before tx-report) id)]
                 (get-state-with-info-message (:db-after tx-report)
                                              "User '" (:name user) "' removed!")))

(castra/defrpc update-user! [user]
               (Thread/sleep 2000)
               (let [tx-report (users/update-user! user)]
                 (get-state-with-info-message (:db-after tx-report)
                                              "User '" (:name user) "' updated!")))
