(ns hopfuse.users
  (:require [tailrecursion.castra :as castra]
            [hopfuse.api :as api]
            [hopfuse.users-data :as usersdata]
            [hopfuse.db-support :as dbsupport]
            ))

(castra/defrpc remove-user! [id]
               {:rpc/pre [(api/logged-in? :role/admin)]}
               (let [tx-report (usersdata/remove-user! id)]
                 (usersdata/find-by-id (:db-before tx-report) id)
                 (api/get-state (:db-after tx-report))))

(castra/defrpc add-user! [add-data]
               {:rpc/pre [(api/logged-in? :role/admin)]}
               ;; maybe to use datomic tx functions here for validation since this is not safe enough?
               (if (usersdata/find-by-username (dbsupport/get-last-db) (:username add-data))
                 (throw (castra/ex castra/error "Username reserved"))
                 (let [new-user (assoc add-data :creation-time (java.util.Date.))
                      tx-report (usersdata/add-user! new-user)]
                  (api/get-state (:db-after tx-report)))))

(castra/defrpc update-user! [update-data]
               ;(Thread/sleep 2000)
               {:rpc/pre [(api/logged-in? :role/admin)]}
               (let [tx-report (usersdata/update-user! update-data)]
                 (api/get-state (:db-after tx-report))))
