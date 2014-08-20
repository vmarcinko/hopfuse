(ns hopfuse.users
  (:require [tailrecursion.castra :as castra]
            [hopfuse.api :as api]
            [hopfuse.users-data :as usersdata]
            [hopfuse.db-support :as dbsupport]
            ))

(castra/defrpc remove-user! [id]
               {:rpc/pre [(api/logged-in? :role/admin)]}
               (let [tx-report (usersdata/remove-user! id)
                     user (usersdata/find-by-id (:db-before tx-report) id)]
                 (api/get-state-with-info-message (:db-after tx-report)
                                                  "User '" (:username user) "' removed!")))

(castra/defrpc add-user! [user]
               {:rpc/pre [(api/logged-in? :role/admin)]}
               (if (usersdata/find-by-username (dbsupport/get-last-db) (:username user))
                 (throw (castra/ex castra/error "Username reserved"))
                 (let [new-user (assoc user :creation-time (java.util.Date.))
                      tx-report (usersdata/add-user! new-user)]
                  (api/get-state-with-info-message (:db-after tx-report)
                                                   "User '" (:username new-user) "' added!"))))

(castra/defrpc update-user! [user]
               ;(Thread/sleep 2000)
               {:rpc/pre [(api/logged-in? :role/admin)]}
               (let [tx-report (usersdata/update-user! user)]
                 (api/get-state-with-info-message (:db-after tx-report)
                                                  "User '" (:username user) "' updated!")))
