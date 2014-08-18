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

(defn update-user! [user]
  @(d/transact dbsupport/conn [{:db/id              (:id user)
                                :user/name          (:name user)
                                :user/last-name     (:last-name user)
                                :user/role          (:role user)}]))

(defn find-by-id [db id]
  (prepare-user-entity (d/entity db id)))

(defn find-by-username [db username]
  (prepare-user-entity (d/entity db [:user/username username])))

(defn find-all [db]
  (map (comp prepare-user-entity (dbsupport/reify-entity-from-id-tuple db))
          (d/q '[:find ?eid
                 :where [?eid :user/username]]
               db)))
