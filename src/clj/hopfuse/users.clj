(ns hopfuse.users
  (:require [tailrecursion.castra :as castra]
            [datomic.api :as d]
            [hopfuse.db-support :as dbsupport]))

(def ^:private user-key-replacements {:db/id          :id
                                      :user/username  :username
                                      :user/password  :password
                                      :user/name      :name
                                      :user/last-name :last-name
                                      :user/role      :role})


(defn- prepare-user-entity [m]
  (dbsupport/rename-selected-keys m user-key-replacements))

(defn remove-user! [id]
  @(d/transact dbsupport/conn [[:db.fn/retractEntity id]]))

(defn find-by-username [username]
  (prepare-user-entity (d/entity (dbsupport/get-last-db) [:user/username username])))

(defn find-all
  ([]
   (find-all (dbsupport/get-last-db)))
  ([db]
   (map (comp prepare-user-entity (dbsupport/reify-entity-from-id-tuple db))
        (d/q '[:find ?eid
               :where [?eid :user/username]]
             db))))
