(ns hopfuse.users
  (:require [tailrecursion.castra :as castra]
            [hopfuse.api :as api]
            [hopfuse.users-data :as usersdata]))

(castra/defrpc remove-user! [id]
               {:rpc/pre [(api/logged-in? :role/admin)]}
               (let [tx-report (usersdata/remove-user! id)
                     user (usersdata/find-by-id (:db-before tx-report) id)]
                 (api/get-state-with-info-message (:db-after tx-report)
                                              "User '" (:name user) "' removed!")))

(castra/defrpc update-user! [user]
               ;(Thread/sleep 2000)
               {:rpc/pre [(api/logged-in?)]}
               (let [tx-report (usersdata/update-user! user)]
                 (api/get-state-with-info-message (:db-after tx-report)
                                              "User '" (:name user) "' updated!")))
